import React from "react";
import { useCurrentFrame, interpolate } from "remotion";
import { EcoAppShell } from "../components/EcoAppShell";
import { EcoCard } from "../components/EcoCard";
import { EcoMetricCard } from "../components/EcoMetricCard";
import { EcoAreaChart } from "../components/EcoAreaChart";
import { EcoBarChart } from "../components/EcoBarChart";
import { EcoTransactionItem } from "../components/EcoTransactionItem";
import { EcoProgressBar } from "../components/EcoProgressBar";
import { summary, metrics, cashFlowData, transactions, categorySpending, budgets, formatCurrency, dashboardMonthLabel } from "../data/demo-data";

export const SceneThemeSwitch: React.FC = () => {
  const frame = useCurrentFrame();
  const progress = interpolate(frame, [0, 45], [0, 1], { extrapolateRight: "clamp" });

  const LightDashboard = () => (
    <EcoAppShell monthLabel={dashboardMonthLabel}>
      <EcoCard gradient wide>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
          <div>
            <span style={{ fontSize: 11, color: "rgba(255,255,255,0.75)", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600 }}>{dashboardMonthLabel}</span>
            <p style={{ fontSize: 13, opacity: 0.75, margin: "8px 0 0" }}>Saldo do mês</p>
            <h1 style={{ fontSize: 36, fontWeight: 800, margin: "4px 0 0", letterSpacing: "-0.02em" }}>{formatCurrency(summary.result)}</h1>
          </div>
        </div>
        <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 16, marginTop: 16 }}>
          <div><p style={{ fontSize: 12, color: "rgba(255,255,255,0.7)", margin: 0 }}>Receitas</p><p style={{ fontSize: 18, fontWeight: 700, margin: "4px 0 0" }}>{formatCurrency(summary.income)}</p></div>
          <div><p style={{ fontSize: 12, color: "rgba(255,255,255,0.7)", margin: 0 }}>Despesas</p><p style={{ fontSize: 18, fontWeight: 700, margin: "4px 0 0" }}>{formatCurrency(summary.expense)}</p></div>
          <div><p style={{ fontSize: 12, color: "rgba(255,255,255,0.7)", margin: 0 }}>Orçamento</p><p style={{ fontSize: 18, fontWeight: 700, margin: "4px 0 0" }}>45%</p></div>
        </div>
      </EcoCard>
      <div style={{ display: "grid", gridTemplateColumns: "1.6fr 1fr", gap: 16, marginTop: 16, height: "calc(100% - 200px)" }}>
        <div style={{ display: "grid", gap: 16, alignContent: "start" }}>
          <EcoCard wide>
            <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 4px" }}>Fluxo de Caixa</p>
            <h2 style={{ margin: "0 0 12px", fontSize: 17, fontWeight: 700, color: "#1A1D2B" }}>Evolução mensal</h2>
            <EcoAreaChart data={cashFlowData} width={580} height={180} />
          </EcoCard>
          <EcoCard wide>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
              <div>
                <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 4px" }}>Transações</p>
                <h2 style={{ margin: 0, fontSize: 17, fontWeight: 700, color: "#1A1D2B" }}>Últimos movimentos</h2>
              </div>
            </div>
            {transactions.slice(0, 3).map((tx) => (
              <EcoTransactionItem key={tx.id} tx={tx} />
            ))}
          </EcoCard>
        </div>
        <div style={{ display: "grid", gap: 16, alignContent: "start" }}>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 12 }}>
            {metrics.map((m) => (
              <EcoMetricCard key={m.label} label={m.label} value={formatCurrency(m.value)} variant={m.variant} />
            ))}
          </div>
          <EcoCard wide>
            <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 4px" }}>Categorias</p>
            <h2 style={{ margin: "0 0 12px", fontSize: 17, fontWeight: 700, color: "#1A1D2B" }}>Onde o dinheiro foi</h2>
            <EcoBarChart data={categorySpending} width={320} height={160} />
          </EcoCard>
          <EcoCard wide>
            <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 12px" }}>Orçamento</p>
            <div style={{ display: "grid", gap: 10 }}>
              {budgets.slice(0, 3).map((b) => (
                <EcoProgressBar key={b.category} label={b.category} value={b.spent} max={b.limit} variant={b.spent / b.limit > 0.9 ? "warning" : "default"} />
              ))}
            </div>
          </EcoCard>
        </div>
      </div>
    </EcoAppShell>
  );

  const DarkDashboard = () => (
    <EcoAppShell monthLabel={dashboardMonthLabel} dark>
      <EcoCard gradient wide>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
          <div>
            <span style={{ fontSize: 11, color: "rgba(255,255,255,0.75)", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600 }}>{dashboardMonthLabel}</span>
            <p style={{ fontSize: 13, opacity: 0.75, margin: "8px 0 0" }}>Saldo do mês</p>
            <h1 style={{ fontSize: 36, fontWeight: 800, margin: "4px 0 0", letterSpacing: "-0.02em" }}>{formatCurrency(summary.result)}</h1>
          </div>
        </div>
        <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 16, marginTop: 16 }}>
          <div><p style={{ fontSize: 12, color: "rgba(255,255,255,0.7)", margin: 0 }}>Receitas</p><p style={{ fontSize: 18, fontWeight: 700, margin: "4px 0 0" }}>{formatCurrency(summary.income)}</p></div>
          <div><p style={{ fontSize: 12, color: "rgba(255,255,255,0.7)", margin: 0 }}>Despesas</p><p style={{ fontSize: 18, fontWeight: 700, margin: "4px 0 0" }}>{formatCurrency(summary.expense)}</p></div>
          <div><p style={{ fontSize: 12, color: "rgba(255,255,255,0.7)", margin: 0 }}>Orçamento</p><p style={{ fontSize: 18, fontWeight: 700, margin: "4px 0 0" }}>45%</p></div>
        </div>
      </EcoCard>
      <div style={{ display: "grid", gridTemplateColumns: "1.6fr 1fr", gap: 16, marginTop: 16, height: "calc(100% - 200px)" }}>
        <div style={{ display: "grid", gap: 16, alignContent: "start" }}>
          <EcoCard wide>
            <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 4px" }}>Fluxo de Caixa</p>
            <h2 style={{ margin: "0 0 12px", fontSize: 17, fontWeight: 700, color: "#F3F4F8" }}>Evolução mensal</h2>
            <EcoAreaChart data={cashFlowData} width={580} height={180} />
          </EcoCard>
          <EcoCard wide>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
              <div>
                <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 4px" }}>Transações</p>
                <h2 style={{ margin: 0, fontSize: 17, fontWeight: 700, color: "#F3F4F8" }}>Últimos movimentos</h2>
              </div>
            </div>
            {transactions.slice(0, 3).map((tx) => (
              <EcoTransactionItem key={tx.id} tx={tx} />
            ))}
          </EcoCard>
        </div>
        <div style={{ display: "grid", gap: 16, alignContent: "start" }}>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 12 }}>
            {metrics.map((m) => (
              <EcoMetricCard key={m.label} label={m.label} value={formatCurrency(m.value)} variant={m.variant} />
            ))}
          </div>
          <EcoCard wide>
            <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 4px" }}>Categorias</p>
            <h2 style={{ margin: "0 0 12px", fontSize: 17, fontWeight: 700, color: "#F3F4F8" }}>Onde o dinheiro foi</h2>
            <EcoBarChart data={categorySpending} width={320} height={160} />
          </EcoCard>
          <EcoCard wide>
            <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 12px" }}>Orçamento</p>
            <div style={{ display: "grid", gap: 10 }}>
              {budgets.slice(0, 3).map((b) => (
                <EcoProgressBar key={b.category} label={b.category} value={b.spent} max={b.limit} variant={b.spent / b.limit > 0.9 ? "warning" : "default"} />
              ))}
            </div>
          </EcoCard>
        </div>
      </div>
    </EcoAppShell>
  );

  return (
    <div style={{ position: "relative", width: "100%", height: "100%" }}>
      <div style={{ position: "absolute", inset: 0, opacity: 1 - progress }}>
        <LightDashboard />
      </div>
      <div style={{ position: "absolute", inset: 0, opacity: progress }}>
        <DarkDashboard />
      </div>
    </div>
  );
};