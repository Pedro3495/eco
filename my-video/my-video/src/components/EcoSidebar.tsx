import React from "react";

const items = ["Dashboard", "Transações", "Orçamentos", "Metas", "Contas", "Categorias"];

export const EcoSidebar: React.FC = () => {
  return (
    <div style={{
      width: 220, height: "100%", padding: "28px 18px", display: "flex", flexDirection: "column",
      borderRight: "1px solid #E5E7EB", background: "rgba(255,255,255,0.72)",
      backdropFilter: "blur(18px)"
    }}>
      <div style={{ display: "flex", alignItems: "center", gap: 10, padding: "4px 10px 24px", fontSize: 22, fontWeight: 800, color: "#08683C" }}>
        <span style={{ width: 32, height: 32, borderRadius: 10, background: "linear-gradient(135deg, #0F8F54, #18B66B)", display: "grid", placeItems: "center", color: "#fff", fontSize: 16 }}>E</span>
        Eco
      </div>
      <nav style={{ display: "grid", gap: 8 }}>
        {items.map((item, i) => (
          <div key={item} style={{
            padding: "10px 14px", borderRadius: 10, fontSize: 14, fontWeight: 600,
            color: i === 0 ? "#fff" : "#6B7280",
            background: i === 0 ? "linear-gradient(135deg, #08683C, #0F8F54)" : "transparent",
            boxShadow: i === 0 ? "0 10px 24px rgba(15,143,84,0.24)" : "none"
          }}>
            {item}
          </div>
        ))}
      </nav>
    </div>
  );
};
