"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import {
  AlertCircle,
  ChevronLeft,
  ChevronRight,
  Loader2,
  Pencil,
  Plus,
  Receipt,
  Trash2,
  SlidersHorizontal,
  X,
  TrendingUp,
  TrendingDown
} from "lucide-react";
import { TransactionModal } from "@/components/TransactionModal";
import { BottomNav } from "@/components/BottomNav";
import { ThemeToggle } from "@/components/ThemeToggle";
import { formatCurrency } from "@/lib/format";
import {
  accounts as mockAccounts,
  categories as mockCategories,
  transactions as mockTransactions
} from "@/mocks/finance-data";
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
  const [showFilters, setShowFilters] = useState(false);
  const [isMobile, setIsMobile] = useState(false);
  const [isMockFallback, setIsMockFallback] = useState(false);

  useEffect(() => {
    setIsMobile(window.innerWidth <= 640);
    const handleResize = () => setIsMobile(window.innerWidth <= 640);
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

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
      setIsMockFallback(false);
    } catch (err) {
      void err;
      const fallbackItems = mockTransactions.map((transaction) => ({
        id: transaction.id,
        description: transaction.description,
        amount: transaction.amount,
        type: transaction.type as "INCOME" | "EXPENSE",
        occurredAt: transaction.date,
        accountName: transaction.account,
        categoryName: transaction.category
      }));

      setAccounts(mockAccounts as Account[]);
      setCategories(mockCategories as Category[]);
      setTransactionsPage({
        items: fallbackItems,
        page: 0,
        size: fallbackItems.length,
        totalItems: fallbackItems.length,
        totalPages: 1
      });
      setPage(0);
      setIsMockFallback(true);
      setError(null);
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
    if (isMobile) setShowFilters(false);
  }

  async function clearFilters() {
    setFilters(initialFilters);
    setPage(0);
    await loadData(0, initialFilters);
    if (isMobile) setShowFilters(false);
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

  const activeFiltersCount = Object.values(filters).filter((v) => v !== "" && v !== undefined).length;

  function FilterFields() {
    return (
      <>
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
      </>
    );
  }

  return (
    <>
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
            {isMockFallback && (
              <span className="demo-badge" title="Dados de demonstração">
                <AlertCircle size={12} aria-hidden="true" />
                modo demonstração
              </span>
            )}
            <ThemeToggle />
            <button
              className="button secondary"
              type="button"
              onClick={() => setShowFilters((s) => !s)}
              aria-label="Filtros"
            >
              <SlidersHorizontal size={16} aria-hidden="true" />
              {activeFiltersCount > 0 && (
                <span style={{
                  background: "var(--primary)",
                  color: "#fff",
                  borderRadius: "999px",
                  padding: "1px 6px",
                  fontSize: "0.7rem",
                  fontWeight: 700
                }}>
                  {activeFiltersCount}
                </span>
              )}
            </button>
            <button className="button primary hidden-sm" type="button" onClick={openCreateModal}>
              <Plus size={16} aria-hidden="true" /> Nova
            </button>
          </div>
        </header>

        {/* Desktop Filters */}
        {!isMobile && showFilters && (
          <section className="panel card filters-panel" aria-label="Filtros de transações">
            <div className="filters-grid">
              <FilterFields />
            </div>
            <div className="filters-actions">
              <button className="button secondary" type="button" onClick={clearFilters}>
                Limpar
              </button>
              <button className="button primary" type="button" onClick={applyFilters}>
                Filtrar
              </button>
            </div>
          </section>
        )}

        {/* Mobile Filter Drawer */}
        <AnimatePresence>
          {isMobile && showFilters && (
            <>
              <motion.div
                className="drawer-backdrop"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                onClick={() => setShowFilters(false)}
              />
              <motion.div
                className="drawer"
                initial={{ y: "100%" }}
                animate={{ y: 0 }}
                exit={{ y: "100%" }}
                transition={{ type: "spring", damping: 25, stiffness: 300 }}
              >
                <div className="drawer-handle" />
                <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 16 }}>
                  <h2 style={{ margin: 0, fontSize: "1.05rem", fontWeight: 700 }}>Filtros</h2>
                  <button className="icon-button" type="button" onClick={() => setShowFilters(false)} aria-label="Fechar filtros">
                    <X size={18} aria-hidden="true" />
                  </button>
                </div>
                <div className="filters-grid" style={{ gridTemplateColumns: "1fr" }}>
                  <FilterFields />
                </div>
                <div className="filters-actions" style={{ flexDirection: "column", marginTop: 16 }}>
                  <button className="button primary" type="button" onClick={applyFilters}>
                    Aplicar filtros
                  </button>
                  <button className="button secondary" type="button" onClick={clearFilters}>
                    Limpar
                  </button>
                </div>
              </motion.div>
            </>
          )}
        </AnimatePresence>

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
            <div className="transaction-list" role="list" style={{ gap: 8 }}>
              {transactions.map((transaction, i) => (
                <motion.div
                  key={transaction.id}
                  className="transaction-card"
                  initial={false}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.2, delay: i * 0.03 }}
                >
                  <div
                    className="transaction-card-icon"
                    style={{
                      background: transaction.type === "INCOME" ? "var(--positive-soft)" : "var(--negative-soft)",
                      color: transaction.type === "INCOME" ? "var(--accent-positive)" : "var(--accent-negative)"
                    }}
                  >
                    {transaction.type === "INCOME" ? <TrendingUp size={20} /> : <TrendingDown size={20} />}
                  </div>
                  <div className="transaction-card-body">
                    <div className="transaction-card-title">{transaction.description}</div>
                    <div className="transaction-card-subtitle">
                      {transaction.categoryName} · {transaction.accountName} · {formatDate(transaction.occurredAt)}
                    </div>
                  </div>
                  <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                    <strong className={`transaction-card-value ${transaction.type === "INCOME" ? "text-positive" : "text-negative"}`}>
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
                </motion.div>
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

      <BottomNav />
    </>
  );
}
