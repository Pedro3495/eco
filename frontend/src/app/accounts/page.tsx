"use client";

import { motion } from "framer-motion";
import Link from "next/link";
import { Wallet, Landmark, CreditCard, Banknote, ArrowRight, TrendingUp, TrendingDown } from "lucide-react";
import { accounts as mockAccounts } from "@/mocks/finance-data";
import { BottomNav } from "@/components/BottomNav";
import { ThemeToggle } from "@/components/ThemeToggle";
import { Card } from "@/components/Card";
import { formatCurrency } from "@/lib/format";

const accountIcons: Record<string, typeof Wallet> = {
  CHECKING: Landmark,
  CASH: Banknote,
  CREDIT_CARD: CreditCard,
  INVESTMENT: TrendingUp
};

const accountColors: Record<string, string> = {
  CHECKING: "#5B42C5",
  CASH: "#0ECD74",
  CREDIT_CARD: "#E85D5D",
  INVESTMENT: "#2C6BED"
};

export default function AccountsPage() {
  const totalBalance = mockAccounts.reduce((sum, a) => sum + a.currentBalance, 0);

  return (
    <>
      <main className="shell">
        <header className="topbar">
          <div className="brand">
            <div className="brand-mark" aria-hidden="true">E</div>
            <div>
              <p className="eyebrow">Eco Finanças</p>
              <strong>Contas</strong>
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
              <p className="section-label">Patrimônio Total</p>
              <h1 style={{ fontSize: "2.2rem", fontWeight: 800, margin: "8px 0 0", letterSpacing: "-0.02em" }}>
                {formatCurrency(totalBalance)}
              </h1>
            </div>
          </Card>
        </motion.div>

        <div className="content-grid" style={{ marginTop: 16 }}>
          {mockAccounts.map((account, i) => {
            const Icon = accountIcons[account.type] ?? Wallet;
            const color = accountColors[account.type] ?? "#5B42C5";
            return (
              <motion.div
                key={account.id}
                initial={{ opacity: 0, y: 12 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: i * 0.06 }}
              >
                <Card>
                  <div style={{ display: "flex", alignItems: "center", gap: 12, marginBottom: 12 }}>
                    <div
                      style={{
                        display: "grid",
                        placeItems: "center",
                        width: 44,
                        height: 44,
                        borderRadius: "var(--radius-md)",
                        background: `${color}18`,
                        color,
                        flexShrink: 0
                      }}
                    >
                      <Icon size={22} aria-hidden="true" />
                    </div>
                    <div style={{ minWidth: 0 }}>
                      <h3 style={{ margin: 0, fontSize: "1rem", fontWeight: 700, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
                        {account.name}
                      </h3>
                      <p className="text-sm text-muted" style={{ margin: "2px 0 0", textTransform: "uppercase", letterSpacing: "0.04em", fontSize: "0.7rem" }}>
                        {account.type.replace("_", " ")}
                      </p>
                    </div>
                  </div>
                  <div style={{ display: "flex", alignItems: "baseline", justifyContent: "space-between" }}>
                    <div>
                      <p className="text-sm text-muted" style={{ margin: 0 }}>Saldo atual</p>
                      <p className="text-xl font-bold tabular" style={{ margin: "4px 0 0" }}>
                        {formatCurrency(account.currentBalance)}
                      </p>
                    </div>
                    <div style={{ textAlign: "right" }}>
                      <p className="text-sm text-muted" style={{ margin: 0 }}>Inicial</p>
                      <p className="text-sm font-semibold tabular" style={{ margin: "4px 0 0" }}>
                        {formatCurrency(account.initialBalance)}
                      </p>
                    </div>
                  </div>
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
