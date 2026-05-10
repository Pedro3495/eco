"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Plus, Loader2, AlertCircle, TrendingUp, TrendingDown, Wallet, Receipt } from "lucide-react";
import { budgets, categorySpending, goals, monthlySummary as mockMonthlySummary, transactions as mockTransactions } from "@/mocks/finance-data";
import { TransactionModal } from "@/components/TransactionModal";
import { formatCurrency, formatPercent } from "@/lib/format";
import {
  Account,
  ApiError,
  Category,
  CreateTransactionRequest,
  createTransaction,
  getAccounts,
  getCategories,
  getMonthlySummary,
  getTransactions,
  MonthlySummary,
  Transaction
} from "@/lib/api";

const dashboardMonth = new Date();
const dashboardYear = dashboardMonth.getFullYear();
const dashboardMonthNumber = dashboardMonth.getMonth() + 1;
const dashboardMonthLabel = new Intl.DateTimeFormat("pt-BR", {
  month: "long",
  year: "numeric"
}).format(dashboardMonth);

function formatDate(iso: string) {
  const [y, m, d] = iso.split("-");
  return `${d}/${m}/${y}`;
}

export default function Home() {
  const [summary, setSummary] = useState<MonthlySummary | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isMockFallback, setIsMockFallback] = useState(false);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError(null);
      setIsMockFallback(false);

      try {
        const [summaryData, txData, accountsData, categoriesData] = await Promise.all([
          getMonthlySummary(dashboardYear, dashboardMonthNumber),
          getTransactions({ page: 0, size: 10 }),
          getAccounts(),
          getCategories()
        ]);

        if (cancelled) return;

        setSummary(summaryData);
        setTransactions(txData.items);
        setAccounts(accountsData);
        setCategories(categoriesData);
      } catch (err) {
        if (cancelled) return;

        console.error("Falha ao carregar dados da API:", err);
        setSummary({
          income: mockMonthlySummary.income,
          expense: mockMonthlySummary.expense,
          balance: mockMonthlySummary.result
        });
        setTransactions(
          mockTransactions.map((t) => ({
            id: t.id,
            description: t.description,
            amount: t.amount,
            type: t.type as "INCOME" | "EXPENSE",
            occurredAt: t.date,
            accountName: t.account,
            categoryName: t.category
          }))
        );
        setIsMockFallback(true);
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => { cancelled = true; };
  }, []);

  const summaryIncome = summary?.income ?? 0;
  const summaryExpense = summary?.expense ?? 0;
  const summaryBalance = summary?.balance ?? 0;

  async function refreshDashboard() {
    const [summaryData, txData] = await Promise.all([
      getMonthlySummary(dashboardYear, dashboardMonthNumber),
      getTransactions({ page: 0, size: 10 })
    ]);

    setSummary(summaryData);
    setTransactions(txData.items);
    setIsMockFallback(false);
  }

  function openTransactionModal() {
    setSaveError(null);
    setIsModalOpen(true);
  }

  function closeTransactionModal() {
    if (saving) return;
    setIsModalOpen(false);
    setSaveError(null);
  }

  async function handleCreateTransaction(data: CreateTransactionRequest) {
    setSaving(true);
    setSaveError(null);

    try {
      await createTransaction(data);
      await refreshDashboard();
      setIsModalOpen(false);
    } catch (err) {
      console.error("Falha ao criar transação:", err);
      setSaveError(err instanceof ApiError ? err.message : "Não foi possível salvar a transação.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="shell">
      <header className="topbar">
        <div className="brand">
          <div className="brand-mark" aria-hidden="true">E</div>
          <div>
            <p className="eyebrow">Eco Finanças</p>
            <strong>Dashboard</strong>
          </div>
        </div>
        <div className="toolbar">
          {isMockFallback && (
            <span className="demo-badge" title="Dados de demonstração">
              <AlertCircle size={12} aria-hidden="true" />
              modo demonstração
            </span>
          )}
          <button className="button" type="button" onClick={openTransactionModal} disabled={isMockFallback}>
            <Plus size={16} aria-hidden="true" /> Nova transação
          </button>
          <Link className="button secondary" href="/transactions">
            Ver transações
          </Link>
        </div>
      </header>

      <section className="summary-section" aria-label="Resumo mensal">
        <div className="panel summary-card">
          <div className="summary-header">
            <span className="section-label">{dashboardMonthLabel}</span>
            <h2 className="summary-title">Resumo mensal</h2>
          </div>

          {loading ? (
            <div className="loading-state" aria-busy="true" aria-live="polite">
              <Loader2 size={20} className="spin" aria-hidden="true" />
              <span>Carregando resumo...</span>
            </div>
          ) : (
            <>
              <div className="metric-grid">
                <div className="metric">
                  <span className="metric-label">
                    <TrendingUp size={14} aria-hidden="true" /> Receitas
                  </span>
                  <strong className="metric-value positive">{formatCurrency(summaryIncome)}</strong>
                </div>
                <div className="metric">
                  <span className="metric-label">
                    <TrendingDown size={14} aria-hidden="true" /> Despesas
                  </span>
                  <strong className="metric-value negative">{formatCurrency(summaryExpense)}</strong>
                </div>
                <div className="metric metric--highlight">
                  <span className="metric-label">
                    <Wallet size={14} aria-hidden="true" /> Saldo
                  </span>
                  <strong className={summaryBalance >= 0 ? "metric-value positive" : "metric-value negative"}>
                    {formatCurrency(summaryBalance)}
                  </strong>
                </div>
              </div>

              <div className="budget-row">
                <div className="row">
                  <span className="muted">Orçamento usado</span>
                  <strong>{formatPercent(mockMonthlySummary.budgetUsage)}</strong>
                </div>
                <div className="bar" aria-hidden="true">
                  <span style={{ width: `${Math.min(mockMonthlySummary.budgetUsage, 100)}%` }} />
                </div>
              </div>
            </>
          )}
        </div>
      </section>

      <section className="content-grid">
        <div className="panel card card--wide">
          <div className="card-header">
            <span className="section-label">Transações</span>
            <h2>Últimos movimentos</h2>
          </div>

          {loading ? (
            <div className="loading-state" aria-busy="true" aria-live="polite">
              <Loader2 size={20} className="spin" aria-hidden="true" />
              <span>Carregando transações...</span>
            </div>
          ) : error && !isMockFallback ? (
            <div className="empty-state" role="alert">
              <AlertCircle size={20} aria-hidden="true" />
              <span>{error}</span>
            </div>
          ) : transactions.length === 0 ? (
            <div className="empty-state">
              <Receipt size={20} aria-hidden="true" />
              <span>Nenhuma transação encontrada para este mês.</span>
            </div>
          ) : (
            <div className="transaction-list" role="list">
              {transactions.map((tx) => (
                <div className="transaction-row" role="listitem" key={tx.id}>
                  <div className="transaction-info">
                    <strong className="transaction-desc">{tx.description}</strong>
                    <span className="transaction-meta">
                      {tx.categoryName} · {tx.accountName} · {formatDate(tx.occurredAt)}
                    </span>
                  </div>
                  <strong className={tx.type === "INCOME" ? "value-positive" : "value-negative"}>
                    {tx.type === "INCOME" ? "+" : "-"}
                    {formatCurrency(tx.amount)}
                  </strong>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="panel card">
          <div className="card-header">
            <span className="section-label">Categorias</span>
            <h2>Onde o dinheiro foi</h2>
          </div>
          <div className="list">
            {categorySpending.map((category) => (
              <div className="row" key={category.name}>
                <div>
                  <strong>{category.name}</strong>
                  <p className="muted">{formatCurrency(category.total)} no mês</p>
                </div>
                <span className="category-dot" style={{ backgroundColor: category.color }} aria-hidden="true" />
              </div>
            ))}
          </div>
        </div>

        <div className="panel card">
          <div className="card-header">
            <span className="section-label">Orçamento</span>
            <h2>Limites do mês</h2>
          </div>
          <div className="list">
            {budgets.map((budget) => {
              const usage = Math.min((budget.spent / budget.limit) * 100, 100);
              return (
                <div key={budget.category}>
                  <div className="row">
                    <strong>{budget.category}</strong>
                    <span className="muted">
                      {formatCurrency(budget.spent)} / {formatCurrency(budget.limit)}
                    </span>
                  </div>
                  <div className="bar" aria-hidden="true">
                    <span style={{ width: `${usage}%` }} />
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        <div className="panel card">
          <div className="card-header">
            <span className="section-label">Metas</span>
            <h2>Progresso</h2>
          </div>
          <div className="list">
            {goals.map((goal) => {
              const usage = Math.min((goal.currentAmount / goal.targetAmount) * 100, 100);
              return (
                <div key={goal.name}>
                  <div className="row">
                    <strong>{goal.name}</strong>
                    <span className="muted">{formatPercent(usage)}</span>
                  </div>
                  <div className="bar" aria-hidden="true">
                    <span style={{ width: `${usage}%` }} />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {isModalOpen && (
        <TransactionModal
          accounts={accounts}
          categories={categories}
          saving={saving}
          error={saveError}
          onClose={closeTransactionModal}
          onSubmit={handleCreateTransaction}
        />
      )}
    </main>
  );
}
