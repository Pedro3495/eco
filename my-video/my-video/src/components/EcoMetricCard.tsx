import React from "react";

interface EcoMetricCardProps {
  label: string;
  value: string;
  variant?: "positive" | "negative" | "default";
}

export const EcoMetricCard: React.FC<EcoMetricCardProps> = ({ label, value, variant }) => {
  const color = variant === "positive" ? "#0ECD74" : variant === "negative" ? "#E85D5D" : "#1A1D2B";
  return (
    <div style={{
      padding: 20,
      borderRadius: 14,
      border: "1px solid #E5E7EB",
      background: "#fff",
      boxShadow: "0 2px 12px rgba(26, 29, 43, 0.06)"
    }}>
      <p style={{ margin: 0, fontSize: 12, color: "#6B7280", fontWeight: 600, letterSpacing: "0.04em" }}>{label}</p>
      <p style={{ margin: "8px 0 0", fontSize: 22, fontWeight: 700, color, fontVariantNumeric: "tabular-nums" }}>{value}</p>
    </div>
  );
};
