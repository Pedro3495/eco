"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import Link from "next/link";
import { Wallet, Landmark, CreditCard, Banknote, ArrowRight, TrendingUp, AlertCircle, Loader2 } from "lucide-react";
import { accounts as mockAccounts } from "@/mocks/finance-data";
import { AppFrame } from "@/components/AppFrame";
import { ThemeToggle } from "@/components/ThemeToggle";
import { LogoutButton } from "@/components/LogoutButton";
import { Card } from "@/components/Card";
import { formatCurrency } from "@/lib/format";
import { Account, createAccount, getAccounts } from "@/lib/api";

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
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [isMockFallback, setIsMockFallback] = useState(false);
  const [form, setForm] = useState({
    name: "",
    type: "CHECKING" as NonNullable<Account["type"]>,
    initialBalance: "0"
  });

  async function loadAccounts() {
    setLoading(true);
    setIsMockFallback(false);

    try {
      setAccounts(await getAccounts());
    } catch {
      setAccounts(mockAccounts as Account[]);
      setIsMockFallback(true);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadAccounts();
  }, []);

  async function handleCreateAccount(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);

    try {
      const created = await createAccount({
        name: form.name,
        type: form.type,
        initialBalance: Number(form.initialBalance)
      });
      setAccounts((current) => [created, ...current]);
      setForm({ name: "", type: "CHECKING", initialBalance: "0" });
      setIsMockFallback(false);
    } finally {
      setSaving(false);
    }
  }

  const totalBalance = accounts.reduce((sum, account) => sum + (account.currentBalance ?? account.initialBalance ?? 0), 0);

  return (
    <>
      <AppFrame title="Contas">
        <header className="topbar">
          <div className="brand">
            <div className="brand-mark" aria-hidden="true">E</div>
            <div>
              <p className="eyebrow">Eco Finanças</p>
              <strong>Contas</strong>
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

        <form className="panel card filters-panel" onSubmit={handleCreateAccount}>
          <div className="filters-grid">
            <label>
              Nome
              <input value={form.name} onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))} required />
            </label>
            <label>
              Tipo
              <select value={form.type} onChange={(event) => setForm((current) => ({ ...current, type: event.target.value as NonNullable<Account["type"]> }))}>
                <option value="CHECKING">Conta corrente</option>
                <option value="CASH">Dinheiro</option>
                <option value="CREDIT_CARD">Cartão de crédito</option>
                <option value="INVESTMENT">Investimento</option>
              </select>
            </label>
            <label>
              Saldo inicial
              <input type="number" min="0" step="0.01" value={form.initialBalance} onChange={(event) => setForm((current) => ({ ...current, initialBalance: event.target.value }))} required />
            </label>
          </div>
          <div className="filters-actions">
            <button className="button primary" type="submit" disabled={saving}>
              Criar conta
            </button>
          </div>
        </form>

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
          {loading ? (
            <div className="loading-state" aria-busy="true">
              <Loader2 size={20} className="spin" aria-hidden="true" />
              <span>Carregando contas...</span>
            </div>
          ) : accounts.map((account, i) => {
            const accountType = account.type ?? "CHECKING";
            const Icon = accountIcons[accountType] ?? Wallet;
            const color = accountColors[accountType] ?? "#5B42C5";
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
                        {accountType.replace("_", " ")}
                      </p>
                    </div>
                  </div>
                  <div style={{ display: "flex", alignItems: "baseline", justifyContent: "space-between" }}>
                    <div>
                      <p className="text-sm text-muted" style={{ margin: 0 }}>Saldo atual</p>
                      <p className="text-xl font-bold tabular" style={{ margin: "4px 0 0" }}>
                        {formatCurrency(account.currentBalance ?? account.initialBalance ?? 0)}
                      </p>
                    </div>
                    <div style={{ textAlign: "right" }}>
                      <p className="text-sm text-muted" style={{ margin: 0 }}>Inicial</p>
                      <p className="text-sm font-semibold tabular" style={{ margin: "4px 0 0" }}>
                        {formatCurrency(account.initialBalance ?? 0)}
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
      </AppFrame>
    </>
  );
}
