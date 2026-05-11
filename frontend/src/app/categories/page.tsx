"use client";

import { motion } from "framer-motion";
import Link from "next/link";
import { Tag, ArrowRight } from "lucide-react";
import { categories as mockCategories } from "@/mocks/finance-data";
import { BottomNav } from "@/components/BottomNav";
import { ThemeToggle } from "@/components/ThemeToggle";
import { Card } from "@/components/Card";

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

export default function CategoriesPage() {
  return (
    <>
      <main className="shell">
        <header className="topbar">
          <div className="brand">
            <div className="brand-mark" aria-hidden="true">E</div>
            <div>
              <p className="eyebrow">Eco Finanças</p>
              <strong>Categorias</strong>
            </div>
          </div>
          <div className="toolbar">
            <ThemeToggle />
          </div>
        </header>

        <div className="content-grid">
          {mockCategories.map((category, i) => (
            <motion.div
              key={category.id}
              initial={{ opacity: 0, y: 12 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: i * 0.05 }}
            >
              <Card>
                <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                  <div
                    style={{
                      display: "grid",
                      placeItems: "center",
                      width: 44,
                      height: 44,
                      borderRadius: "var(--radius-md)",
                      background: `${category.color}18`,
                      color: category.color,
                      flexShrink: 0
                    }}
                  >
                    <Tag size={20} aria-hidden="true" />
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <h3 style={{ margin: 0, fontSize: "1rem", fontWeight: 700, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
                      {category.name}
                    </h3>
                    <p className="text-sm text-muted" style={{ margin: "2px 0 0", textTransform: "uppercase", letterSpacing: "0.04em", fontSize: "0.7rem" }}>
                      {category.kind === "INCOME" ? "Receita" : category.kind === "EXPENSE" ? "Despesa" : "Ambos"}
                    </p>
                  </div>
                  <div
                    className="category-dot"
                    style={{ width: 12, height: 12, backgroundColor: category.color }}
                    aria-hidden="true"
                  />
                </div>
              </Card>
            </motion.div>
          ))}
        </div>

        <div style={{ marginTop: 24, textAlign: "center" }}>
          <Link href="/" className="button ghost">
            <ArrowRight size={16} aria-hidden="true" /> Voltar ao dashboard
          </Link>
        </div>
      </main>

      <BottomNav />
    </>
  );
}
