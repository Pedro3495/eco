"use client";

import { Plus } from "lucide-react";

interface FABProps {
  onClick: () => void;
  label?: string;
  disabled?: boolean;
}

export function FAB({ onClick, label = "Nova transação", disabled = false }: FABProps) {
  return (
    <button
      className="fab"
      type="button"
      onClick={onClick}
      aria-label={label}
      disabled={disabled}
    >
      <Plus size={24} aria-hidden="true" />
    </button>
  );
}
