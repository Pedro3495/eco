"use client";

import { motion } from "framer-motion";

interface ProgressBarProps {
  value: number;
  max?: number;
  variant?: "default" | "positive" | "negative" | "warning";
  label?: string;
  showPercentage?: boolean;
}

export function ProgressBar({
  value,
  max = 100,
  variant = "default",
  label,
  showPercentage = true
}: ProgressBarProps) {
  const percentage = Math.min((value / max) * 100, 100);
  const variantClass = variant === "positive"
    ? "bar-positive"
    : variant === "negative"
      ? "bar-negative"
      : variant === "warning"
        ? "bar-warning"
        : "";

  return (
    <div>
      {(label || showPercentage) && (
        <div className="row" style={{ padding: "4px 0" }}>
          {label && <strong className="text-sm">{label}</strong>}
          {showPercentage && (
            <span className="text-sm text-muted">{percentage.toFixed(0)}%</span>
          )}
        </div>
      )}
      <div className={`bar ${variantClass}`}>
        <motion.span
          style={{ width: `${percentage}%` }}
          initial={false}
          animate={{ width: `${percentage}%` }}
          transition={{ duration: 0.8, ease: [0.4, 0, 0.2, 1], delay: 0.2 }}
        />
      </div>
    </div>
  );
}
