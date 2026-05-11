import React from "react";
import { useCurrentFrame, interpolate } from "remotion";
import { EcoAppShell } from "../components/EcoAppShell";
import { EcoCard } from "../components/EcoCard";
import { summary, formatCurrency, dashboardMonthLabel } from "../data/demo-data";

export const SceneThemeSwitch: React.FC = () => {
  const frame = useCurrentFrame();
  const progress = interpolate(frame, [0, 45], [0, 1], { extrapolateRight: "clamp" });

  return (
    <div style={{ position: "relative", width: "100%", height: "100%" }}>
      <div style={{ position: "absolute", inset: 0, opacity: 1 - progress }}>
        <EcoAppShell monthLabel={dashboardMonthLabel}>
          <EcoCard gradient wide>
            <h1 style={{ fontSize: 36, fontWeight: 800, margin: 0 }}>{formatCurrency(summary.result)}</h1>
          </EcoCard>
        </EcoAppShell>
      </div>
      <div style={{ position: "absolute", inset: 0, opacity: progress }}>
        <EcoAppShell monthLabel={dashboardMonthLabel} dark>
          <EcoCard gradient wide>
            <h1 style={{ fontSize: 36, fontWeight: 800, margin: 0 }}>{formatCurrency(summary.result)}</h1>
          </EcoCard>
        </EcoAppShell>
      </div>
    </div>
  );
};
