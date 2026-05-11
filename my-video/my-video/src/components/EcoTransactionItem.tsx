import React from "react";

interface Tx {
  description: string;
  category: string;
  account: string;
  type: "INCOME" | "EXPENSE";
  amount: number;
  date: string;
}

interface Props {
  tx: Tx;
}

export const EcoTransactionItem: React.FC<Props> = ({ tx }) => {
  const isIncome = tx.type === "INCOME";
  const color = isIncome ? "#0ECD74" : "#E85D5D";
  const bg = isIncome ? "rgba(14,205,116,0.08)" : "rgba(232,93,93,0.08)";

  return (
    <div style={{
      display: "flex", alignItems: "center", gap: 14, padding: "14px 16px",
      borderBottom: "1px solid #F0F1F5"
    }}>
      <div style={{
        width: 40, height: 40, borderRadius: 10, display: "grid", placeItems: "center",
        background: bg, color, flexShrink: 0, fontWeight: 700, fontSize: 14
      }}>
        {isIncome ? "↑" : "↓"}
      </div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontWeight: 600, fontSize: 14, color: "#1A1D2B", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>{tx.description}</div>
        <div style={{ fontSize: 12, color: "#6B7280", marginTop: 2 }}>{tx.category} · {tx.account} · {tx.date}</div>
      </div>
      <div style={{ fontWeight: 700, fontSize: 14, color, fontVariantNumeric: "tabular-nums", whiteSpace: "nowrap" }}>
        {isIncome ? "+" : "-"}R$ {tx.amount.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
      </div>
    </div>
  );
};
