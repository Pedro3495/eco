"use client";

import { motion } from "framer-motion";
import { TrendingUp, TrendingDown } from "lucide-react";

interface TransactionCardProps {
  id: string;
  description: string;
  amount: number;
  type: "INCOME" | "EXPENSE";
  categoryName?: string;
  accountName?: string;
  date?: string;
  color?: string;
  icon?: string;
  onClick?: () => void;
}

const iconMap: Record<string, string> = {
  utensils: "utensils",
  car: "car",
  home: "home",
  "gamepad-2": "gamepad-2",
  monitor: "monitor",
  "heart-pulse": "heart-pulse",
  "trending-up": "trending-up",
  "more-horizontal": "more-horizontal"
};

export function TransactionCard({
  description,
  amount,
  type,
  categoryName,
  accountName,
  date,
  color = "#5B42C5",
  onClick
}: TransactionCardProps) {
  function formatDate(iso: string) {
    if (!iso) return "";
    const [y, m, d] = iso.split("-");
    return `${d}/${m}/${y}`;
  }

  function formatCurrency(val: number) {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL"
    }).format(val);
  }

  return (
    <motion.div
      className="transaction-card"
      onClick={onClick}
      style={{ cursor: onClick ? "pointer" : "default" }}
      initial={false}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.2, ease: "easeOut" }}
      whileTap={{ scale: 0.98 }}
    >
      <div
        className="transaction-card-icon"
        style={{ background: `${color}18`, color }}
        aria-hidden="true"
      >
        {type === "INCOME" ? <TrendingUp size={20} /> : <TrendingDown size={20} />}
      </div>
      <div className="transaction-card-body">
        <div className="transaction-card-title">{description}</div>
        <div className="transaction-card-subtitle">
          {[categoryName, accountName, date ? formatDate(date) : null]
            .filter(Boolean)
            .join(" · ")}
        </div>
      </div>
      <div className={`transaction-card-value ${type === "INCOME" ? "text-positive" : "text-negative"}`}>
        {type === "INCOME" ? "+" : "-"}
        {formatCurrency(amount)}
      </div>
    </motion.div>
  );
}
