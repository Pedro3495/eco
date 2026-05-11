import React from "react";
import { useCurrentFrame, interpolate } from "remotion";
import { EcoAppShell } from "../components/EcoAppShell";
import { EcoCard } from "../components/EcoCard";
import { EcoMetricCard } from "../components/EcoMetricCard";
import { EcoAreaChart } from "../components/EcoAreaChart";
import { EcoTransactionItem } from "../components/EcoTransactionItem";
import { summary, metrics, cashFlowData, transactions, formatCurrency, dashboardMonthLabel } from "../data/demo-data";

export const SceneDashboard: React.FC = () => {
  const frame = useCurrentFrame();

  const fade = (start: number, end: number) => interpolate(frame, [start, end], [0, 1], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });
  const slideY = (start: number, end: number) => interpolate(frame, [start, end], [30, 0], { extrapolateLeft: "clamp", extrapolateRight: "clamp" });

  return (
    <EcoAppShell monthLabel={dashboardMonthLabel}>
      <div style={{ opacity: fade(0, 30), transform: `translateY(${slideY(0, 30)}px)` }}>
        <EcoCard gradient wide>
          <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 16 }}>
            <span style={{ fontSize: 11, color: "rgba(255,255,255,0.75)", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600 }}>{dashboardMonthLabel}</span>
            <span style={{ fontSize: 18, opacity: 0.6 }}>👛</span>
          </div>
          <p style={{ fontSize: 13, opacity: 0.75, margin: 0 }}>Saldo do mês</p>
          <h1 style={{ fontSize: 36, fontWeight: 800, margin: "4px 0 0", letterSpacing: "-0.02em" }}>{formatCurrency(summary.result)}</h1>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 16, marginTop: 20 }}>
            <div><p style={{ fontSize: 12, color: "rgba(255,255,255,0.7)", margin: 0 }}>Receitas</p><p style={{ fontSize: 18, fontWeight: 700, margin: "4px 0 0" }}>{formatCurrency(summary.income)}</p></div>
            <div><p style={{ fontSize: 12, color: "rgba(255,255,255,0.7)", margin: 0 }}>Despesas</p><p style={{ fontSize: 18, fontWeight: 700, margin: "4px 0 0" }}>{formatCurrency(summary.expense)}</p></div>
            <div><p style={{ fontSize: 12, color: "rgba(255,255,255,0.7)", margin: 0 }}>Orçamento</p><p style={{ fontSize: 18, fontWeight: 700, margin: "4px 0 0" }}>45%</p></div>
          </div>
        </EcoCard>
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 14, marginTop: 16 }}>
        {metrics.map((m, i) => (
          <div key={m.label} style={{ opacity: fade(20 + i * 8, 50 + i * 8), transform: `translateY(${slideY(20 + i * 8, 50 + i * 8)}px)` }}>
            <EcoMetricCard label={m.label} value={formatCurrency(m.value)} variant={m.variant} />
          </div>
        ))}
      </div>

      <div style={{ marginTop: 16, opacity: fade(50, 80), transform: `translateY(${slideY(50, 80)}px)` }}>
        <EcoCard wide>
          <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 4px" }}>Fluxo de Caixa</p>
          <h2 style={{ margin: "0 0 16px", fontSize: 17, fontWeight: 700 }}>Evolução mensal</h2>
          <EcoAreaChart data={cashFlowData} width={700} height={220} />
        </EcoCard>
      </div>

      <div style={{ marginTop: 16, opacity: fade(80, 110), transform: `translateY(${slideY(80, 110)}px)` }}>
        <EcoCard wide>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
            <div>
              <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 4px" }}>Transações</p>
              <h2 style={{ margin: 0, fontSize: 17, fontWeight: 700 }}>Últimos movimentos</h2>
            </div>
            <span style={{ fontSize: 12, color: "#6B7280", fontWeight: 600 }}>Ver tudo →</span>
          </div>
          {transactions.slice(0, 4).map((tx, i) => (
            <div key={tx.id} style={{ opacity: fade(100 + i * 6, 130 + i * 6), transform: `translateX(${interpolate(frame, [100 + i * 6, 130 + i * 6], [20, 0], { extrapolateLeft: "clamp", extrapolateRight: "clamp" })}px)` }}>
              <EcoTransactionItem tx={tx} />
            </div>
          ))}
        </EcoCard>
      </div>
    </EcoAppShell>
  );
};
