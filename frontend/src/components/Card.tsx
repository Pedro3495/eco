"use client";

import { motion } from "framer-motion";
import type { ReactNode } from "react";

interface CardProps {
  children: ReactNode;
  className?: string;
  gradient?: boolean;
  wide?: boolean;
}

export function Card({ children, className = "", gradient = false, wide = false }: CardProps) {
  return (
    <motion.div
      className={`panel card ${gradient ? "card-gradient" : ""} ${wide ? "card--wide" : ""} ${className}`}
      initial={false}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.25, ease: "easeOut" }}
    >
      {children}
    </motion.div>
  );
}
