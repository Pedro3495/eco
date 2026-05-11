"use client";

import { motion } from "framer-motion";
import type { ReactNode } from "react";

interface MetricCardProps {
  label: ReactNode;
  value: string;
  variant?: "default" | "positive" | "negative" | "highlight";
  delay?: number;
}

export function MetricCard({ label, value, variant = "default", delay = 0 }: MetricCardProps) {
  const valueClass = variant === "positive"
    ? "metric-value positive"
    : variant === "negative"
      ? "metric-value negative"
      : "metric-value";

  return (
    <motion.div
      className={`metric ${variant === "highlight" ? "metric--highlight" : ""}`}
      initial={false}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.25, delay, ease: "easeOut" }}
    >
      <span className="metric-label">{label}</span>
      <strong className={valueClass}>{value}</strong>
    </motion.div>
  );
}
