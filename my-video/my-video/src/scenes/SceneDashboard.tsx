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

export const SceneDashboard: React.FC = () => {
  const frame = useCurrentFrame();

  const fade = (start: number, end: number) => interpolate(frame, [start, end], [0, 1], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });
  const slideY = (start: number, end: number) => interpolate(frame, [start, end], [30, 0], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });
  const grow = (start: number, end: number) => interpolate(frame, [start, end], [0, 1], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });

  return (
    <EcoAppShell monthLabel={dashboardMonthLabel}>
      {/* Row 1: Main balance card */}
      <div style={{ opacity: fade(0, 25), transform: `translateY(${slideY(0, 25)}px)` }}>
        <EcoCard gradient wide>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
            <div>
              <span style={{ fontSize: 11, color: "rgba(255,255,255,0.75)", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600 }}>{dashboardMonthLabel}</span>
              <p style={{ fontSize: 13, opacity: 0.75, margin: "8px 0 0" }}>Saldo do mês</p>
              <h1 style={{ fontSize: 36, fontWeight: 800, margin: "4px 0 0", letterSpacing: "-0.02em" }}>{formatCurrency(summary.result)}</h1>
            </div>
            <span style={{ fontSize: 24, opacity: 0.6 }}>👛</span>
          </div>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 16, marginTop: 16 }}>
            <div><p style={{ fontSize: 12, color: "rgba(255,255,255,0.7)", margin: 0 }}>Receitas</p><p style={{ fontSize: 18, fontWeight: 700, margin: "4px 0 0" }}>{formatCurrency(summary.income)}</p></div>
            <div><p style={{ fontSize: 12, color: "rgba(255,255,255,0.7)", margin: 0 }}>Despesas</p><p style={{ fontSize: 18, fontWeight: 700, margin: "4px 0 0" }}>{formatCurrency(summary.expense)}</p></div>
            <div><p style={{ fontSize: 12, color: "rgba(255,255,255,0.7)", margin: 0 }}>Orçamento</p><p style={{ fontSize: 18, fontWeight: 700, margin: "4px 0 0" }}>45%</p></div>
          </div>
        </EcoCard>
      </div>

      {/* Row 2: Two columns */}
      <div style={{ display: "grid", gridTemplateColumns: "1.6fr 1fr", gap: 16, marginTop: 16, height: "calc(100% - 200px)" }}>
        {/* Left column */}
        <div style={{ display: "grid", gap: 16, alignContent: "start" }}>
          {/* Cash flow chart */}
          <div style={{ opacity: fade(30, 55), transform: `translateY(${slideY(30, 55)}px)` }}>
            <EcoCard wide>
              <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 4px" }}>Fluxo de Caixa</p>
              <h2 style={{ margin: "0 0 12px", fontSize: 17, fontWeight: 700, color: "#1A1D2B" }}>Evolução mensal</h2>
              <EcoAreaChart data={cashFlowData} width={580} height={180} />
            </EcoCard>
          </div>

          {/* Transactions */}
          <div style={{ opacity: fade(55, 80), transform: `translateY(${slideY(55, 80)}px)` }}>
            <EcoCard wide>
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
                <div>
                  <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 4px" }}>Transações</p>
                  <h2 style={{ margin: 0, fontSize: 17, fontWeight: 700, color: "#1A1D2B" }}>Últimos movimentos</h2>
                </div>
                <span style={{ fontSize: 12, color: "#6B7280", fontWeight: 600 }}>Ver tudo →</span>
              </div>
              {transactions.slice(0, 4).map((tx, i) => (
                <div key={tx.id} style={{ opacity: fade(70 + i * 5, 95 + i * 5), transform: `translateX(${interpolate(frame, [70 + i * 5, 95 + i * 5], [20, 0], { extrapolateLeft: "clamp", extrapolateRight: "clamp" })}px)` }}>
                  <EcoTransactionItem tx={tx} />
                </div>
              ))}
            </EcoCard>
          </div>
        </div>

        {/* Right column */}
        <div style={{ display: "grid", gap: 16, alignContent: "start" }}>
          {/* Metrics */}
          <div style={{ opacity: fade(20, 45), transform: `translateY(${slideY(20, 45)}px)` }}>
            <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 12 }}>
              {metrics.map((m, i) => (
                <div key={m.label} style={{ opacity: fade(25 + i * 6, 45 + i * 6) }}>
                  <EcoMetricCard label={m.label} value={formatCurrency(m.value)} variant={m.variant} />
                </div>
              ))}
            </div>
          </div>

          {/* Categories */}
          <div style={{ opacity: fade(50, 75), transform: `translateY(${slideY(50, 75)}px)` }}>
            <EcoCard wide>
              <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 4px" }}>Categorias</p>
              <h2 style={{ margin: "0 0 12px", fontSize: 17, fontWeight: 700, color: "#1A1D2B" }}>Onde o dinheiro foi</h2>
              <EcoBarChart data={categorySpending.map(c => ({ ...c, total: c.total * grow(50, 80) }))} width={320} height={160} />
            </EcoCard>
          </div>

          {/* Budgets */}
          <div style={{ opacity: fade(75, 100), transform: `translateY(${slideY(75, 100)}px)` }}>
            <EcoCard wide>
              <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 12px" }}>Orçamento</p>
              <h2 style={{ margin: "0 0 12px", fontSize: 17, fontWeight: 700, color: "#1A1D2B" }}>Limites do mês</h2>
              <div style={{ display: "grid", gap: 10 }}>
                {budgets.map((b) => (
                  <EcoProgressBar key={b.category} label={b.category} value={b.spent} max={b.limit} variant={b.spent / b.limit > 0.9 ? "warning" : "default"} />
                ))}
              </div>
            </EcoCard>
          </div>
        </div>
      </div>
    </EcoAppShell>
  );
};