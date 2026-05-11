"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { motion } from "framer-motion";
import {
  Plus,
  Loader2,
  AlertCircle,
  TrendingUp,
  TrendingDown,
  Wallet,
  Receipt,
  ArrowRight,
  CreditCard,
  Target
} from "lucide-react";
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  BarChart,
  Bar,
  Cell
} from "recharts";
import {
  budgets,
  categorySpending,
  goals,
  monthlySummary as mockMonthlySummary,
  transactions as mockTransactions,
  cashFlowData
} from "@/mocks/finance-data";
import { TransactionModal } from "@/components/TransactionModal";
import { ThemeToggle } from "@/components/ThemeToggle";
import { FAB } from "@/components/FAB";
import { BottomNav } from "@/components/BottomNav";
import { Card } from "@/components/Card";
import { MetricCard } from "@/components/MetricCard";
import { ProgressBar } from "@/components/ProgressBar";
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

const containerVariants = {
  hidden: { opacity: 0 },
  show: {
    opacity: 1,
    transition: { staggerChildren: 0.08 }
  }
};

const itemVariants = {
  hidden: { opacity: 0, y: 16 },
  show: { opacity: 1, y: 0, transition: { duration: 0.4, ease: "easeOut" as const } }
};

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
  const [chartsReady, setChartsReady] = useState(false);

  useEffect(() => {
    setChartsReady(true);
  }, []);

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

        void err;
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
    try {
      const [summaryData, txData] = await Promise.all([
        getMonthlySummary(dashboardYear, dashboardMonthNumber),
        getTransactions({ page: 0, size: 10 })
      ]);
      setSummary(summaryData);
      setTransactions(txData.items);
      setIsMockFallback(false);
    } catch {
      // mantém mocks
    }
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

  const totalBudget = budgets.reduce((sum, b) => sum + b.limit, 0);
  const totalSpent = budgets.reduce((sum, b) => sum + b.spent, 0);
  const activeGoals = goals.filter((g) => g.status === "ACTIVE");

  return (
    <>
      <main className="shell">
        {/* Header */}
        <motion.header
          className="topbar"
          initial={false}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.25 }}
        >
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
            <ThemeToggle />
            <button
              className="button primary hidden-sm"
              type="button"
              onClick={openTransactionModal}
              disabled={isMockFallback}
            >
              <Plus size={16} aria-hidden="true" /> Nova transação
            </button>
          </div>
        </motion.header>

        <motion.section
          variants={containerVariants}
          initial={false}
          animate="show"
          aria-label="Resumo mensal"
        >
          {/* Saldo Principal */}
          <motion.div variants={itemVariants}>
            <Card gradient wide>
              <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 16 }}>
                <span className="section-label" style={{ color: "rgba(255,255,255,0.75)" }}>{dashboardMonthLabel}</span>
                <Wallet size={20} aria-hidden="true" style={{ opacity: 0.6 }} />
              </div>
              <div style={{ marginBottom: 8 }}>
                <p style={{ fontSize: "0.85rem", opacity: 0.75, margin: 0 }}>Saldo do mês</p>
                <h1 style={{ fontSize: "2rem", fontWeight: 800, margin: "4px 0 0", letterSpacing: "-0.02em" }}>
                  {formatCurrency(summaryBalance)}
                </h1>
              </div>
              <div className="metric-grid" style={{ marginTop: 20, marginBottom: 0 }}>
                <div>
                  <p className="text-xs" style={{ color: "rgba(255,255,255,0.7)" }}>Receitas</p>
                  <p className="text-lg font-bold tabular" style={{ marginTop: 4 }}>{formatCurrency(summaryIncome)}</p>
                </div>
                <div>
                  <p className="text-xs" style={{ color: "rgba(255,255,255,0.7)" }}>Despesas</p>
                  <p className="text-lg font-bold tabular" style={{ marginTop: 4 }}>{formatCurrency(summaryExpense)}</p>
                </div>
                <div>
                  <p className="text-xs" style={{ color: "rgba(255,255,255,0.7)" }}>Orçamento</p>
                  <p className="text-lg font-bold tabular" style={{ marginTop: 4 }}>{formatPercent(mockMonthlySummary.budgetUsage)}</p>
                </div>
              </div>
            </Card>
          </motion.div>

          {/* Métricas */}
          <motion.div variants={itemVariants} style={{ marginTop: 16 }}>
            <div className="metric-grid" style={{ gridTemplateColumns: "repeat(3, 1fr)" }}>
              <MetricCard
                label={<><TrendingUp size={14} aria-hidden="true" /> Receitas</>}
                value={formatCurrency(summaryIncome)}
                variant="positive"
                delay={0}
              />
              <MetricCard
                label={<><TrendingDown size={14} aria-hidden="true" /> Despesas</>}
                value={formatCurrency(summaryExpense)}
                variant="negative"
                delay={0.05}
              />
              <MetricCard
                label={<><Wallet size={14} aria-hidden="true" /> Resultado</>}
                value={formatCurrency(summaryBalance)}
                variant={summaryBalance >= 0 ? "positive" : "negative"}
                delay={0.1}
              />
            </div>
          </motion.div>

          {/* Gráfico de Fluxo de Caixa */}
          <motion.div variants={itemVariants} style={{ marginTop: 16 }}>
            <Card wide>
              <div className="card-header">
                <span className="section-label">Fluxo de Caixa</span>
                <h2>Evolução mensal</h2>
              </div>
              <div className="chart-container">
                {chartsReady && (
                  <ResponsiveContainer width="100%" height="100%" minWidth={0} minHeight={220}>
                    <AreaChart data={cashFlowData} margin={{ top: 5, right: 5, left: -20, bottom: 0 }}>
                    <defs>
                      <linearGradient id="colorIncome" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#0ECD74" stopOpacity={0.2}/>
                        <stop offset="95%" stopColor="#0ECD74" stopOpacity={0}/>
                      </linearGradient>
                      <linearGradient id="colorExpense" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#E85D5D" stopOpacity={0.2}/>
                        <stop offset="95%" stopColor="#E85D5D" stopOpacity={0}/>
                      </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" stroke="var(--line)" />
                    <XAxis dataKey="month" tick={{ fontSize: 12, fill: "var(--ink-muted)" }} axisLine={false} tickLine={false} />
                    <YAxis tick={{ fontSize: 12, fill: "var(--ink-muted)" }} axisLine={false} tickLine={false} />
                    <Tooltip
                      contentStyle={{
                        background: "var(--surface)",
                        border: "1px solid var(--line)",
                        borderRadius: "var(--radius-md)",
                        fontSize: 13
                      }}
                      formatter={(value) => typeof value === 'number' ? formatCurrency(value) : String(value)}
                    />
                    <Area type="monotone" dataKey="income" stroke="#0ECD74" strokeWidth={2} fillOpacity={1} fill="url(#colorIncome)" />
                    <Area type="monotone" dataKey="expense" stroke="#E85D5D" strokeWidth={2} fillOpacity={1} fill="url(#colorExpense)" />
                    </AreaChart>
                  </ResponsiveContainer>
                )}
              </div>
            </Card>
          </motion.div>

          {/* Grid de conteúdo */}
          <div className="content-grid" style={{ marginTop: 16 }}>
            {/* Últimas transações */}
            <motion.div variants={itemVariants} className="card--wide">
              <Card wide>
                <div className="card-header" style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                  <div>
                    <span className="section-label">Transações</span>
                    <h2>Últimos movimentos</h2>
                  </div>
                  <Link href="/transactions" className="button ghost sm">
                    Ver tudo <ArrowRight size={14} aria-hidden="true" />
                  </Link>
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
                    {transactions.slice(0, 5).map((tx, i) => (
                      <motion.div
                        key={tx.id}
                        className="transaction-card"
                        initial={false}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.2, delay: i * 0.04 }}
                      >
                        <div
                          className="transaction-card-icon"
                          style={{
                            background: tx.type === "INCOME" ? "var(--positive-soft)" : "var(--negative-soft)",
                            color: tx.type === "INCOME" ? "var(--accent-positive)" : "var(--accent-negative)"
                          }}
                        >
                          {tx.type === "INCOME" ? <TrendingUp size={20} /> : <TrendingDown size={20} />}
                        </div>
                        <div className="transaction-card-body">
                          <div className="transaction-card-title">{tx.description}</div>
                          <div className="transaction-card-subtitle">
                            {tx.categoryName} · {tx.accountName} · {formatDate(tx.occurredAt)}
                          </div>
                        </div>
                        <div className={`transaction-card-value ${tx.type === "INCOME" ? "text-positive" : "text-negative"}`}>
                          {tx.type === "INCOME" ? "+" : "-"}
                          {formatCurrency(tx.amount)}
                        </div>
                      </motion.div>
                    ))}
                  </div>
                )}
              </Card>
            </motion.div>

            {/* Gastos por categoria */}
            <motion.div variants={itemVariants}>
              <Card>
                <div className="card-header">
                  <span className="section-label">Categorias</span>
                  <h2>Onde o dinheiro foi</h2>
                </div>
                <div className="chart-container-sm">
                  {chartsReady && (
                    <ResponsiveContainer width="100%" height="100%" minWidth={0} minHeight={160}>
                      <BarChart data={categorySpending} layout="vertical" margin={{ top: 0, right: 10, left: 0, bottom: 0 }}>
                      <CartesianGrid strokeDasharray="3 3" stroke="var(--line)" horizontal={false} />
                      <XAxis type="number" hide />
                      <YAxis
                        dataKey="name"
                        type="category"
                        tick={{ fontSize: 11, fill: "var(--ink-secondary)" }}
                        width={90}
                        axisLine={false}
                        tickLine={false}
                      />
                      <Tooltip
                        contentStyle={{
                          background: "var(--surface)",
                          border: "1px solid var(--line)",
                          borderRadius: "var(--radius-md)",
                          fontSize: 13
                        }}
                        formatter={(value) => typeof value === 'number' ? formatCurrency(value) : String(value)}
                      />
                      <Bar dataKey="total" radius={[0, 6, 6, 0]} barSize={18}>
                        {categorySpending.map((entry, index) => (
                          <Cell key={`cell-${index}`} fill={entry.color} />
                        ))}
                      </Bar>
                      </BarChart>
                    </ResponsiveContainer>
                  )}
                </div>
              </Card>
            </motion.div>

            {/* Orçamento */}
            <motion.div variants={itemVariants}>
              <Card>
                <div className="card-header">
                  <span className="section-label">Orçamento</span>
                  <h2>Limites do mês</h2>
                </div>
                <div className="list" style={{ gap: 16 }}>
                  <ProgressBar
                    value={totalSpent}
                    max={totalBudget}
                    label="Geral"
                    variant={totalSpent / totalBudget > 0.9 ? "warning" : "default"}
                  />
                  {budgets.map((budget) => (
                    <ProgressBar
                      key={budget.category}
                      value={budget.spent}
                      max={budget.limit}
                      label={budget.category}
                      variant={budget.spent / budget.limit > 0.9 ? "warning" : "default"}
                    />
                  ))}
                </div>
                <div style={{ marginTop: 16, textAlign: "center" }}>
                  <Link href="/budgets" className="button ghost sm" style={{ width: "100%" }}>
                    Ver detalhes <ArrowRight size={14} aria-hidden="true" />
                  </Link>
                </div>
              </Card>
            </motion.div>

            {/* Metas */}
            <motion.div variants={itemVariants}>
              <Card>
                <div className="card-header">
                  <span className="section-label">Metas</span>
                  <h2>Progresso</h2>
                </div>
                <div className="list" style={{ gap: 16 }}>
                  {activeGoals.map((goal) => (
                    <ProgressBar
                      key={goal.id}
                      value={goal.currentAmount}
                      max={goal.targetAmount}
                      label={goal.name}
                      variant={goal.currentAmount >= goal.targetAmount ? "positive" : "default"}
                    />
                  ))}
                </div>
                <div style={{ marginTop: 16, textAlign: "center" }}>
                  <Link href="/goals" className="button ghost sm" style={{ width: "100%" }}>
                    Ver metas <ArrowRight size={14} aria-hidden="true" />
                  </Link>
                </div>
              </Card>
            </motion.div>

            {/* Total do cartão */}
            <motion.div variants={itemVariants}>
              <Card>
                <div className="card-header" style={{ display: "flex", alignItems: "center", gap: 10 }}>
                  <CreditCard size={20} aria-hidden="true" className="text-primary" />
                  <div>
                    <span className="section-label">Cartão de Crédito</span>
                    <h2>Fatura atual</h2>
                  </div>
                </div>
                <div style={{ textAlign: "center", padding: "20px 0" }}>
                  <p className="text-3xl font-extrabold tabular text-negative" style={{ margin: 0 }}>
                    {formatCurrency(mockMonthlySummary.creditCardTotal)}
                  </p>
                  <p className="text-sm text-muted" style={{ marginTop: 8 }}>
                    Vence em 10/06/2026
                  </p>
                </div>
                <div style={{ marginTop: 8, textAlign: "center" }}>
                  <Link href="/accounts" className="button ghost sm" style={{ width: "100%" }}>
                    Ver contas <ArrowRight size={14} aria-hidden="true" />
                  </Link>
                </div>
              </Card>
            </motion.div>

            {/* Resumo rápido de metas */}
            <motion.div variants={itemVariants}>
              <Card>
                <div className="card-header" style={{ display: "flex", alignItems: "center", gap: 10 }}>
                  <Target size={20} aria-hidden="true" className="text-primary" />
                  <div>
                    <span className="section-label">Resumo</span>
                    <h2>Metas ativas</h2>
                  </div>
                </div>
                <div className="list">
                  {activeGoals.map((goal) => (
                    <div key={goal.id} className="row">
                      <div>
                        <strong className="text-sm">{goal.name}</strong>
                        <p className="text-sm text-muted" style={{ margin: "2px 0 0" }}>
                          {formatCurrency(goal.currentAmount)} de {formatCurrency(goal.targetAmount)}
                        </p>
                      </div>
                      <span className="text-sm font-semibold text-primary">
                        {((goal.currentAmount / goal.targetAmount) * 100).toFixed(0)}%
                      </span>
                    </div>
                  ))}
                </div>
              </Card>
            </motion.div>
          </div>
        </motion.section>

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

      <FAB onClick={openTransactionModal} disabled={isMockFallback} />
      <BottomNav />
    </>
  );
}
