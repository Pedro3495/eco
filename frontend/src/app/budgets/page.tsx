"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import Link from "next/link";
import { AlertCircle, ArrowRight, Loader2, Tag } from "lucide-react";
import { budgets as mockBudgets } from "@/mocks/finance-data";
import { AppFrame } from "@/components/AppFrame";
import { ThemeToggle } from "@/components/ThemeToggle";
import { LogoutButton } from "@/components/LogoutButton";
import { Card } from "@/components/Card";
import { ProgressBar } from "@/components/ProgressBar";
import { formatCurrency } from "@/lib/format";
import { BudgetSummary, Category, getBudgetSummary, getCategories, upsertCategoryBudget, upsertMonthlyBudget } from "@/lib/api";

const currentMonth = new Date();
const currentMonthParam = `${currentMonth.getFullYear()}-${String(currentMonth.getMonth() + 1).padStart(2, "0")}`;

export default function BudgetsPage() {
  const [budget, setBudget] = useState<BudgetSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [isMockFallback, setIsMockFallback] = useState(false);
  const [categories, setCategories] = useState<Category[]>([]);
  const [generalLimit, setGeneralLimit] = useState("");
  const [categoryForm, setCategoryForm] = useState({ categoryId: "", limitAmount: "" });

  useEffect(() => {
    let cancelled = false;

    async function loadBudget() {
      setLoading(true);
      setIsMockFallback(false);

      try {
        const [response, categoriesData] = await Promise.all([
          getBudgetSummary(currentMonthParam),
          getCategories()
        ]);
        if (!cancelled) {
          setBudget(response);
          setCategories(categoriesData.filter((category) => category.kind !== "INCOME"));
          setGeneralLimit(response.generalLimit ? String(response.generalLimit) : "");
        }
      } catch {
        if (!cancelled) {
          setBudget(null);
          getCategories().then((data) => setCategories(data.filter((category) => category.kind !== "INCOME"))).catch(() => undefined);
          setIsMockFallback(true);
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    loadBudget();
    return () => { cancelled = true; };
  }, []);

  const realCategories = budget?.categories.map((category) => ({
    category: category.categoryName,
    limit: category.limitAmount,
    spent: category.spentAmount
  })) ?? [];
  const visibleBudgets = realCategories.length > 0 ? realCategories : mockBudgets;
  const totalLimit = budget?.generalLimit ?? visibleBudgets.reduce((sum, b) => sum + b.limit, 0);
  const totalSpent = budget?.totalSpent ?? visibleBudgets.reduce((sum, b) => sum + b.spent, 0);

  async function reloadBudget() {
    const response = await getBudgetSummary(currentMonthParam);
    setBudget(response);
    setGeneralLimit(response.generalLimit ? String(response.generalLimit) : "");
  }

  async function handleGeneralBudget(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);

    try {
      await upsertMonthlyBudget(currentMonthParam, Number(generalLimit));
      await reloadBudget();
      setIsMockFallback(false);
    } finally {
      setSaving(false);
    }
  }

  async function handleCategoryBudget(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);

    try {
      await upsertCategoryBudget(currentMonthParam, categoryForm.categoryId, Number(categoryForm.limitAmount));
      await reloadBudget();
      setCategoryForm({ categoryId: "", limitAmount: "" });
      setIsMockFallback(false);
    } finally {
      setSaving(false);
    }
  }

  return (
    <>
      <AppFrame title="Orcamentos">
        <header className="topbar">
          <div className="brand">
            <div className="brand-mark" aria-hidden="true">E</div>
            <div>
              <p className="eyebrow">Eco Finanças</p>
              <strong>Orçamentos</strong>
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
            <LogoutButton />
          </div>
        </header>

        <motion.div
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
        >
          <Card wide>
            <div style={{ textAlign: "center", padding: "20px 0" }}>
              <p className="section-label">Orçamento Mensal</p>
              <h1 style={{ fontSize: "2rem", fontWeight: 800, margin: "8px 0 0", letterSpacing: "-0.02em" }}>
                {loading ? "..." : formatCurrency(totalSpent)} <span className="text-lg text-muted" style={{ fontWeight: 500 }}>/ {formatCurrency(totalLimit)}</span>
              </h1>
              <div style={{ maxWidth: 320, margin: "16px auto 0" }}>
                <ProgressBar
                  value={totalSpent}
                  max={totalLimit}
                  showPercentage={false}
                  variant={totalSpent / totalLimit > 0.9 ? "warning" : "default"}
                />
              </div>
            </div>
          </Card>
        </motion.div>

        <section className="panel card filters-panel">
          <form onSubmit={handleGeneralBudget}>
            <div className="filters-grid">
              <label>
                Limite geral
                <input type="number" min="0" step="0.01" value={generalLimit} onChange={(event) => setGeneralLimit(event.target.value)} required />
              </label>
            </div>
            <div className="filters-actions">
              <button className="button primary" type="submit" disabled={saving}>
                Salvar limite geral
              </button>
            </div>
          </form>
          <form onSubmit={handleCategoryBudget} style={{ marginTop: 16 }}>
            <div className="filters-grid">
              <label>
                Categoria
                <select value={categoryForm.categoryId} onChange={(event) => setCategoryForm((current) => ({ ...current, categoryId: event.target.value }))} required>
                  <option value="">Selecione</option>
                  {categories.map((category) => (
                    <option key={category.id} value={category.id}>{category.name}</option>
                  ))}
                </select>
              </label>
              <label>
                Limite da categoria
                <input type="number" min="0.01" step="0.01" value={categoryForm.limitAmount} onChange={(event) => setCategoryForm((current) => ({ ...current, limitAmount: event.target.value }))} required />
              </label>
            </div>
            <div className="filters-actions">
              <button className="button secondary" type="submit" disabled={saving}>
                Salvar categoria
              </button>
            </div>
          </form>
        </section>

        <div className="content-grid" style={{ marginTop: 16 }}>
          {loading ? (
            <div className="loading-state" aria-busy="true">
              <Loader2 size={20} className="spin" aria-hidden="true" />
              <span>Carregando orçamentos...</span>
            </div>
          ) : visibleBudgets.map((budget, i) => (
            <motion.div
              key={budget.category}
              initial={{ opacity: 0, y: 12 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: i * 0.06 }}
            >
              <Card>
                <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 12 }}>
                  <Tag size={18} aria-hidden="true" className="text-primary" />
                  <h3 style={{ margin: 0, fontSize: "1rem", fontWeight: 700 }}>{budget.category}</h3>
                </div>
                <div style={{ marginBottom: 12 }}>
                  <p className="text-sm text-muted" style={{ margin: 0 }}>
                    {formatCurrency(budget.spent)} de {formatCurrency(budget.limit)}
                  </p>
                </div>
                <ProgressBar
                  value={budget.spent}
                  max={budget.limit}
                  showPercentage={true}
                  variant={budget.spent / budget.limit > 0.9 ? "warning" : "default"}
                />
              </Card>
            </motion.div>
          ))}
        </div>

        <div style={{ marginTop: 24, textAlign: "center" }}>
          <Link href="/" className="button ghost">
            <ArrowRight size={16} aria-hidden="true" /> Voltar ao dashboard
          </Link>
        </div>
      </AppFrame>
    </>
  );
}
