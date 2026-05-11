import React from "react";

interface Props {
  monthLabel: string;
}

export const EcoTopbar: React.FC<Props> = ({ monthLabel }) => {
  return (
    <div style={{
      display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 24
    }}>
      <div>
        <p style={{ margin: 0, fontSize: 11, color: "#6B7280", letterSpacing: "0.08em", textTransform: "uppercase", fontWeight: 600 }}>Eco Finanças</p>
        <h1 style={{ margin: "4px 0 0", fontSize: 22, fontWeight: 700, color: "#1A1D2B" }}>Dashboard</h1>
      </div>
      <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
        <span style={{ fontSize: 12, color: "#6B7280", fontWeight: 600 }}>{monthLabel}</span>
        <div style={{ width: 34, height: 34, borderRadius: 8, background: "#F0F1F5", display: "grid", placeItems: "center", fontSize: 12 }}>☀</div>
        <div style={{ display: "flex", alignItems: "center", gap: 8, fontSize: 13, color: "#6B7280", fontWeight: 600 }}>
          <span style={{ width: 30, height: 30, borderRadius: "50%", background: "rgba(15,143,84,0.1)", color: "#0F8F54", display: "grid", placeItems: "center", fontSize: 12 }}>A</span>
          Olá, Ana
        </div>
      </div>
    </div>
  );
};
