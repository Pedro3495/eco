import React from "react";
import { useCurrentFrame, interpolate } from "remotion";
import { EcoAppShell } from "../components/EcoAppShell";
import { EcoCard } from "../components/EcoCard";
import { EcoBarChart } from "../components/EcoBarChart";
import { EcoProgressBar } from "../components/EcoProgressBar";
import { categorySpending, budgets, goals, dashboardMonthLabel } from "../data/demo-data";

export const SceneBudgets: React.FC = () => {
  const frame = useCurrentFrame();

  const barGrow = interpolate(frame, [0, 40], [0, 1], { extrapolateRight: "clamp" });
  const listFade = interpolate(frame, [30, 60], [0, 1], { extrapolateRight: "clamp" });

  const scaledData = categorySpending.map(d => ({ ...d, total: d.total * barGrow }));

  return (
    <EcoAppShell monthLabel={dashboardMonthLabel} dark>
      <div style={{ display: "grid", gridTemplateColumns: "1.2fr 1fr", gap: 20, height: "calc(100% - 60px)" }}>
        <EcoCard wide style={{ height: "fit-content" }}>
          <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 4px" }}>Categorias</p>
          <h2 style={{ margin: "0 0 16px", fontSize: 17, fontWeight: 700, color: "#F3F4F8" }}>Onde o dinheiro foi</h2>
          <EcoBarChart data={scaledData} width={480} height={200} />
        </EcoCard>

        <div style={{ opacity: listFade, display: "grid", gap: 16 }}>
          <EcoCard>
            <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 12px" }}>Orçamento</p>
            <h2 style={{ margin: "0 0 16px", fontSize: 17, fontWeight: 700, color: "#F3F4F8" }}>Limites do mês</h2>
            <div style={{ display: "grid", gap: 14 }}>
              {budgets.map((b) => (
                <EcoProgressBar key={b.category} label={b.category} value={b.spent} max={b.limit} variant={b.spent / b.limit > 0.9 ? "warning" : "default"} />
              ))}
            </div>
          </EcoCard>

          <EcoCard>
            <p style={{ fontSize: 11, color: "#6B7280", letterSpacing: "0.06em", textTransform: "uppercase", fontWeight: 600, margin: "0 0 12px" }}>Metas</p>
            <h2 style={{ margin: "0 0 16px", fontSize: 17, fontWeight: 700, color: "#F3F4F8" }}>Progresso</h2>
            <div style={{ display: "grid", gap: 14 }}>
              {goals.map((g) => (
                <EcoProgressBar key={g.id} label={g.name} value={g.current} max={g.target} variant={g.current >= g.target ? "positive" : "default"} />
              ))}
            </div>
          </EcoCard>
        </div>
      </div>
    </EcoAppShell>
  );
};
