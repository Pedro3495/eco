"use client";

import { motion } from "framer-motion";
import Link from "next/link";
import { Target, CheckCircle2, ArrowRight } from "lucide-react";
import { goals as mockGoals } from "@/mocks/finance-data";
import { BottomNav } from "@/components/BottomNav";
import { ThemeToggle } from "@/components/ThemeToggle";
import { Card } from "@/components/Card";
import { ProgressBar } from "@/components/ProgressBar";
import { formatCurrency } from "@/lib/format";

export default function GoalsPage() {
  return (
    <>
      <main className="shell">
        <header className="topbar">
          <div className="brand">
            <div className="brand-mark" aria-hidden="true">E</div>
            <div>
              <p className="eyebrow">Eco Finanças</p>
              <strong>Metas</strong>
            </div>
          </div>
          <div className="toolbar">
            <ThemeToggle />
          </div>
        </header>

        <div className="content-grid">
          {mockGoals.map((goal, i) => {
            const percentage = Math.min((goal.currentAmount / goal.targetAmount) * 100, 100);
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
      </main>

      <BottomNav />
    </>
  );
}
