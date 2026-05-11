"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import Link from "next/link";
import { AlertCircle, Target, CheckCircle2, ArrowRight, Loader2 } from "lucide-react";
import { goals as mockGoals } from "@/mocks/finance-data";
import { AppFrame } from "@/components/AppFrame";
import { ThemeToggle } from "@/components/ThemeToggle";
import { LogoutButton } from "@/components/LogoutButton";
import { Card } from "@/components/Card";
import { ProgressBar } from "@/components/ProgressBar";
import { formatCurrency } from "@/lib/format";
import { createGoal, Goal, getGoals, updateGoalProgress } from "@/lib/api";

export default function GoalsPage() {
  const [goals, setGoals] = useState<Goal[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [isMockFallback, setIsMockFallback] = useState(false);
  const [form, setForm] = useState({ name: "", targetAmount: "", currentAmount: "0", targetDate: "" });

  useEffect(() => {
    loadGoals();
  }, []);

  async function loadGoals() {
      setLoading(true);
      setIsMockFallback(false);

      try {
        const response = await getGoals();
        setGoals(response);
      } catch {
        setGoals(
          mockGoals.map((goal) => ({
            ...goal,
            status: goal.status as Goal["status"],
            progressPercent: (goal.currentAmount / goal.targetAmount) * 100
          }))
        );
        setIsMockFallback(true);
      } finally {
        setLoading(false);
      }
  }

  async function handleCreateGoal(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);

    try {
      const created = await createGoal({
        name: form.name,
        targetAmount: Number(form.targetAmount),
        currentAmount: Number(form.currentAmount || 0),
        targetDate: form.targetDate || null
      });
      setGoals((current) => [created, ...current]);
      setForm({ name: "", targetAmount: "", currentAmount: "0", targetDate: "" });
      setIsMockFallback(false);
    } finally {
      setSaving(false);
    }
  }

  async function handleProgress(goal: Goal) {
    const value = window.prompt("Novo valor atual", String(goal.currentAmount));
    if (value === null) return;
    const currentAmount = Number(value);
    if (Number.isNaN(currentAmount)) return;

    const updated = await updateGoalProgress(goal.id, currentAmount);
    setGoals((current) => current.map((item) => item.id === updated.id ? updated : item));
  }

  return (
    <>
      <AppFrame title="Metas">
        <header className="topbar">
          <div className="brand">
            <div className="brand-mark" aria-hidden="true">E</div>
            <div>
              <p className="eyebrow">Eco Finanças</p>
              <strong>Metas</strong>
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

        <form className="panel card filters-panel" onSubmit={handleCreateGoal}>
          <div className="filters-grid">
            <label>
              Nome
              <input value={form.name} onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))} required />
            </label>
            <label>
              Alvo
              <input type="number" min="0.01" step="0.01" value={form.targetAmount} onChange={(event) => setForm((current) => ({ ...current, targetAmount: event.target.value }))} required />
            </label>
            <label>
              Atual
              <input type="number" min="0" step="0.01" value={form.currentAmount} onChange={(event) => setForm((current) => ({ ...current, currentAmount: event.target.value }))} required />
            </label>
            <label>
              Prazo
              <input type="date" value={form.targetDate} onChange={(event) => setForm((current) => ({ ...current, targetDate: event.target.value }))} />
            </label>
          </div>
          <div className="filters-actions">
            <button className="button primary" type="submit" disabled={saving || isMockFallback}>
              Criar meta
            </button>
          </div>
        </form>

        <div className="content-grid">
          {loading ? (
            <div className="loading-state" aria-busy="true">
              <Loader2 size={20} className="spin" aria-hidden="true" />
              <span>Carregando metas...</span>
            </div>
          ) : goals.map((goal, i) => {
            const percentage = Math.min(goal.progressPercent, 100);
            const isCompleted = goal.status === "COMPLETED";
            return (
              <motion.div
                key={goal.id}
                initial={{ opacity: 0, y: 12 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: i * 0.06 }}
              >
                <Card>
                  <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 12 }}>
                    {isCompleted ? (
                      <CheckCircle2 size={22} aria-hidden="true" className="text-positive" />
                    ) : (
                      <Target size={22} aria-hidden="true" className="text-primary" />
                    )}
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <h3 style={{ margin: 0, fontSize: "1rem", fontWeight: 700, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
                        {goal.name}
                      </h3>
                      <p className="text-sm text-muted" style={{ margin: "2px 0 0" }}>
                        {isCompleted ? "Concluída" : goal.targetDate ? `Até ${goal.targetDate}` : "Sem prazo"}
                      </p>
                    </div>
                    {isCompleted && (
                      <span
                        style={{
                          padding: "2px 8px",
                          borderRadius: "var(--radius-sm)",
                          background: "var(--positive-soft)",
                          color: "var(--accent-positive)",
                          fontSize: "0.7rem",
                          fontWeight: 700,
                          textTransform: "uppercase",
                          letterSpacing: "0.04em",
                          whiteSpace: "nowrap"
                        }}
                      >
                        Concluída
                      </span>
                    )}
                  </div>
                  <div style={{ marginBottom: 12 }}>
                    <p className="text-sm text-muted" style={{ margin: 0 }}>
                      {formatCurrency(goal.currentAmount)} de {formatCurrency(goal.targetAmount)}
                    </p>
                  </div>
                  <ProgressBar
                    value={goal.currentAmount}
                    max={goal.targetAmount}
                    showPercentage={true}
                    variant={isCompleted ? "positive" : "default"}
                  />
                  <button className="button secondary sm" type="button" onClick={() => handleProgress(goal)} disabled={isMockFallback} style={{ marginTop: 12, width: "100%" }}>
                    Atualizar progresso
                  </button>
                </Card>
              </motion.div>
            );
          })}
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
