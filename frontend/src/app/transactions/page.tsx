"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { AlertCircle, ChevronLeft, ChevronRight, Loader2, Pencil, Plus, Receipt, Trash2 } from "lucide-react";
import { TransactionModal } from "@/components/TransactionModal";
import { formatCurrency } from "@/lib/format";
import {
  Account,
  ApiError,
  Category,
  CreateTransactionRequest,
  createTransaction,
  deleteTransaction,
  getAccounts,
  getCategories,
  getTransactions,
  PaginatedTransactions,
  Transaction,
  TransactionFilters,
  updateTransaction,
  UpdateTransactionRequest
} from "@/lib/api";

type TransactionTypeFilter = "" | "INCOME" | "EXPENSE";

interface FiltersState {
  startDate: string;
  endDate: string;
  type: TransactionTypeFilter;
  accountId: string;
  categoryId: string;
}

const initialFilters: FiltersState = {
  startDate: "",
  endDate: "",
  type: "",
  accountId: "",
  categoryId: ""
};

function formatDate(iso: string) {
  const [year, month, day] = iso.split("-");
  return `${day}/${month}/${year}`;
}

function getErrorMessage(error: unknown, fallback: string) {
  return error instanceof ApiError ? error.message : fallback;
}

export default function TransactionsPage() {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [transactionsPage, setTransactionsPage] = useState<PaginatedTransactions | null>(null);
  const [filters, setFilters] = useState(initialFilters);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [modalTransaction, setModalTransaction] = useState<Transaction | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalError, setModalError] = useState<string | null>(null);

  async function loadData(nextPage = page, nextFilters = filters) {
    setLoading(true);
    setError(null);

    try {
      const txFilters: TransactionFilters = {
        page: nextPage,
        size: 10,
        startDate: nextFilters.startDate || undefined,
        endDate: nextFilters.endDate || undefined,
        type: nextFilters.type || undefined,
        accountId: nextFilters.accountId || undefined,
        categoryId: nextFilters.categoryId || undefined
      };

      const [accountsData, categoriesData, txData] = await Promise.all([
        getAccounts(),
        getCategories(),
        getTransactions(txFilters)
      ]);

      setAccounts(accountsData);
      setCategories(categoriesData);
      setTransactionsPage(txData);
      setPage(txData.page);
    } catch (err) {
      console.error("Falha ao carregar transações:", err);
      setError(getErrorMessage(err, "Não foi possível carregar as transações."));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadData(0, initialFilters);
  }, []);

  function updateFilter(field: keyof FiltersState, value: string) {
    setFilters((current) => ({ ...current, [field]: value }));
  }

  async function applyFilters() {
    setPage(0);
    await loadData(0, filters);
  }

  async function clearFilters() {
    setFilters(initialFilters);
    setPage(0);
    await loadData(0, initialFilters);
  }

  function openCreateModal() {
    setModalTransaction(null);
    setModalError(null);
    setIsModalOpen(true);
  }

  function openEditModal(transaction: Transaction) {
    setModalTransaction(transaction);
    setModalError(null);
    setIsModalOpen(true);
  }

  function closeModal() {
    if (actionLoading) return;
    setIsModalOpen(false);
    setModalTransaction(null);
    setModalError(null);
  }

  async function handleModalSubmit(data: CreateTransactionRequest | UpdateTransactionRequest) {
    setActionLoading(true);
    setModalError(null);

    try {
      if (modalTransaction) {
        await updateTransaction(modalTransaction.id, data as UpdateTransactionRequest);
      } else {
        await createTransaction(data as CreateTransactionRequest);
      }

      await loadData(page, filters);
      closeModal();
    } catch (err) {
      console.error("Falha ao salvar transação:", err);
      setModalError(getErrorMessage(err, "Não foi possível salvar a transação."));
    } finally {
      setActionLoading(false);
    }
  }

  async function handleDelete(transaction: Transaction) {
    const confirmed = window.confirm(`Excluir "${transaction.description}"?`);
    if (!confirmed) return;

    setActionLoading(true);
    setError(null);

    try {
      await deleteTransaction(transaction.id);
      await loadData(page, filters);
    } catch (err) {
      console.error("Falha ao excluir transação:", err);
      setError(getErrorMessage(err, "Não foi possível excluir a transação."));
    } finally {
      setActionLoading(false);
    }
  }

  async function goToPage(nextPage: number) {
    await loadData(nextPage, filters);
  }

  const transactions = transactionsPage?.items ?? [];
  const totalPages = transactionsPage?.totalPages ?? 0;
  const canGoBack = page > 0;
  const canGoForward = totalPages > 0 && page + 1 < totalPages;

  return (
    <main className="shell">
      <header className="topbar">
        <div className="brand">
          <div className="brand-mark" aria-hidden="true">E</div>
          <div>
            <p className="eyebrow">Eco Finanças</p>
            <strong>Transações</strong>
          </div>
        </div>
        <div className="toolbar">
          <Link className="button secondary" href="/">
            Dashboard
          </Link>
          <button className="button" type="button" onClick={openCreateModal}>
            <Plus size={16} aria-hidden="true" /> Nova transação
          </button>
        </div>
      </header>

      <section className="panel card filters-panel" aria-label="Filtros de transações">
        <div className="filters-grid">
          <label htmlFor="filter-start-date">
            Início
            <input
              id="filter-start-date"
              type="date"
              value={filters.startDate}
              onChange={(event) => updateFilter("startDate", event.target.value)}
            />
          </label>
          <label htmlFor="filter-end-date">
            Fim
            <input
              id="filter-end-date"
              type="date"
              value={filters.endDate}
              onChange={(event) => updateFilter("endDate", event.target.value)}
            />
          </label>
          <label htmlFor="filter-type">
            Tipo
            <select id="filter-type" value={filters.type} onChange={(event) => updateFilter("type", event.target.value)}>
              <option value="">Todos</option>
              <option value="EXPENSE">Despesa</option>
              <option value="INCOME">Receita</option>
            </select>
          </label>
          <label htmlFor="filter-account">
            Conta
            <select id="filter-account" value={filters.accountId} onChange={(event) => updateFilter("accountId", event.target.value)}>
              <option value="">Todas</option>
              {accounts.map((account) => (
                <option key={account.id} value={account.id}>{account.name}</option>
              ))}
            </select>
          </label>
          <label htmlFor="filter-category">
            Categoria
            <select id="filter-category" value={filters.categoryId} onChange={(event) => updateFilter("categoryId", event.target.value)}>
              <option value="">Todas</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>{category.name}</option>
              ))}
            </select>
          </label>
        </div>
        <div className="filters-actions">
          <button className="button secondary" type="button" onClick={clearFilters}>
            Limpar
          </button>
          <button className="button" type="button" onClick={applyFilters}>
            Filtrar
          </button>
        </div>
      </section>

      <section className="panel card" aria-label="Lista de transações">
        <div className="card-header row">
          <div>
            <span className="section-label">Histórico</span>
            <h2>Movimentos cadastrados</h2>
          </div>
          {transactionsPage && (
            <span className="muted">{transactionsPage.totalItems} registro(s)</span>
          )}
        </div>

        {loading ? (
          <div className="loading-state" aria-busy="true" aria-live="polite">
            <Loader2 size={20} className="spin" aria-hidden="true" />
            <span>Carregando transações...</span>
          </div>
        ) : error ? (
          <div className="empty-state" role="alert">
            <AlertCircle size={20} aria-hidden="true" />
            <span>{error}</span>
          </div>
        ) : transactions.length === 0 ? (
          <div className="empty-state">
            <Receipt size={20} aria-hidden="true" />
            <span>Nenhuma transação encontrada.</span>
          </div>
        ) : (
          <div className="transaction-list" role="list">
            {transactions.map((transaction) => (
              <div className="transaction-row transaction-row--actions" role="listitem" key={transaction.id}>
                <div className="transaction-info">
                  <strong className="transaction-desc">{transaction.description}</strong>
                  <span className="transaction-meta">
                    {transaction.categoryName} · {transaction.accountName} · {formatDate(transaction.occurredAt)}
                  </span>
                </div>
                <strong className={transaction.type === "INCOME" ? "value-positive" : "value-negative"}>
                  {transaction.type === "INCOME" ? "+" : "-"}
                  {formatCurrency(transaction.amount)}
                </strong>
                <div className="row-actions">
                  <button
                    className="icon-button"
                    type="button"
                    onClick={() => openEditModal(transaction)}
                    aria-label={`Editar transação ${transaction.description}`}
                    disabled={actionLoading}
                  >
                    <Pencil size={16} aria-hidden="true" />
                  </button>
                  <button
                    className="icon-button danger"
                    type="button"
                    onClick={() => handleDelete(transaction)}
                    aria-label={`Excluir transação ${transaction.description}`}
                    disabled={actionLoading}
                  >
                    <Trash2 size={16} aria-hidden="true" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        <div className="pagination">
          <button
            className="button secondary"
            type="button"
            onClick={() => goToPage(page - 1)}
            disabled={!canGoBack || loading}
            aria-label="Página anterior"
          >
            <ChevronLeft size={16} aria-hidden="true" /> Anterior
          </button>
          <span className="muted" aria-live="polite">
            Página {totalPages === 0 ? 0 : page + 1} de {totalPages}
          </span>
          <button
            className="button secondary"
            type="button"
            onClick={() => goToPage(page + 1)}
            disabled={!canGoForward || loading}
            aria-label="Próxima página"
          >
            Próxima <ChevronRight size={16} aria-hidden="true" />
          </button>
        </div>
      </section>

      {isModalOpen && (
        <TransactionModal
          accounts={accounts}
          categories={categories}
          transaction={modalTransaction}
          saving={actionLoading}
          error={modalError}
          onClose={closeModal}
          onSubmit={handleModalSubmit}
        />
      )}
    </main>
  );
}
