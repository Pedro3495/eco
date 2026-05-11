"use client";

import { motion } from "framer-motion";
import Link from "next/link";
import { ArrowRight, Tag } from "lucide-react";
import { budgets as mockBudgets } from "@/mocks/finance-data";
import { BottomNav } from "@/components/BottomNav";
import { ThemeToggle } from "@/components/ThemeToggle";
import { Card } from "@/components/Card";
import { ProgressBar } from "@/components/ProgressBar";
import { formatCurrency } from "@/lib/format";

export default function BudgetsPage() {
  const totalLimit = mockBudgets.reduce((sum, b) => sum + b.limit, 0);
  const totalSpent = mockBudgets.reduce((sum, b) => sum + b.spent, 0);

  return (
    <>
      <main className="shell">
        <header className="topbar">
          <div className="brand">
            <div className="brand-mark" aria-hidden="true">E</div>
            <div>
              <p className="eyebrow">Eco Finanças</p>
              <strong>Orçamentos</strong>
            </div>
          </div>
          <div className="toolbar">
            <ThemeToggle />
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
                {formatCurrency(totalSpent)} <span className="text-lg text-muted" style={{ fontWeight: 500 }}>/ {formatCurrency(totalLimit)}</span>
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

        <div className="content-grid" style={{ marginTop: 16 }}>
          {mockBudgets.map((budget, i) => (
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
      </main>

      <BottomNav />
    </>
  );
}
