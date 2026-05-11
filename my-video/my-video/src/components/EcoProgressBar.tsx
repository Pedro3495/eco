import React from "react";

interface EcoProgressBarProps {
  value: number;
  max: number;
  label: string;
  variant?: "default" | "warning" | "positive";
}

export const EcoProgressBar: React.FC<EcoProgressBarProps> = ({ value, max, label, variant }) => {
  const pct = Math.min((value / max) * 100, 100);
  const barColor = variant === "warning" ? "linear-gradient(90deg, #F59E0B, #FBBF24)"
    : variant === "positive" ? "linear-gradient(90deg, #0ECD74, #34D399)"
    : "linear-gradient(90deg, #0F8F54, #18B66B)";

  return (
    <div>
      <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 6 }}>
        <span style={{ fontSize: 14, fontWeight: 600, color: "#1A1D2B" }}>{label}</span>
        <span style={{ fontSize: 13, color: "#6B7280" }}>{pct.toFixed(0)}%</span>
      </div>
      <div style={{ height: 8, borderRadius: 999, background: "#F0F1F5", overflow: "hidden" }}>
        <div style={{ height: "100%", width: `${pct}%`, borderRadius: 999, background: barColor }} />
      </div>
    </div>
  );
};
