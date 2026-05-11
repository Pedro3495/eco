"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import Link from "next/link";
import { Tag, ArrowRight, AlertCircle, Loader2 } from "lucide-react";
import { categories as mockCategories } from "@/mocks/finance-data";
import { AppFrame } from "@/components/AppFrame";
import { ThemeToggle } from "@/components/ThemeToggle";
import { LogoutButton } from "@/components/LogoutButton";
import { Card } from "@/components/Card";
import { Category, createCategory, getCategories } from "@/lib/api";

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
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [isMockFallback, setIsMockFallback] = useState(false);
  const [form, setForm] = useState({
    name: "",
    kind: "EXPENSE" as Category["kind"],
    color: "#0f8f53",
    icon: "tag"
  });

  async function loadCategories() {
    setLoading(true);
    setIsMockFallback(false);

    try {
      setCategories(await getCategories());
    } catch {
      setCategories(mockCategories as Category[]);
      setIsMockFallback(true);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadCategories();
  }, []);

  async function handleCreateCategory(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);

    try {
      const created = await createCategory(form);
      setCategories((current) => [created, ...current]);
      setForm({ name: "", kind: "EXPENSE", color: "#0f8f53", icon: "tag" });
      setIsMockFallback(false);
    } finally {
      setSaving(false);
    }
  }

  return (
    <>
      <AppFrame title="Categorias">
        <header className="topbar">
          <div className="brand">
            <div className="brand-mark" aria-hidden="true">E</div>
            <div>
              <p className="eyebrow">Eco Finanças</p>
              <strong>Categorias</strong>
            </div>
          </div>
          <div className="toolbar">
            {isMockFallback && (
              <span className="demo-badge" title="Dados de demonstração">
                <AlertCircle size={12} aria-hidden="true" />
                modo demonstração
              </span>
            )}
            <ThemeToggle />
            <LogoutButton />
          </div>
        </header>

        <form className="panel card filters-panel" onSubmit={handleCreateCategory}>
          <div className="filters-grid">
            <label>
              Nome
              <input value={form.name} onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))} required />
            </label>
            <label>
              Tipo
              <select value={form.kind} onChange={(event) => setForm((current) => ({ ...current, kind: event.target.value as Category["kind"] }))}>
                <option value="EXPENSE">Despesa</option>
                <option value="INCOME">Receita</option>
                <option value="BOTH">Ambos</option>
              </select>
            </label>
            <label>
              Cor
              <input type="color" value={form.color} onChange={(event) => setForm((current) => ({ ...current, color: event.target.value }))} />
            </label>
            <label>
              Ícone
              <input value={form.icon} onChange={(event) => setForm((current) => ({ ...current, icon: event.target.value }))} />
            </label>
          </div>
          <div className="filters-actions">
            <button className="button primary" type="submit" disabled={saving}>
              Criar categoria
            </button>
          </div>
        </form>

        <div className="content-grid">
          {loading ? (
            <div className="loading-state" aria-busy="true">
              <Loader2 size={20} className="spin" aria-hidden="true" />
              <span>Carregando categorias...</span>
            </div>
          ) : categories.map((category, i) => (
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
                      background: `${category.color ?? "#0f8f53"}18`,
                      color: category.color ?? "#0f8f53",
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
                    style={{ width: 12, height: 12, backgroundColor: category.color ?? "#0f8f53" }}
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
      </AppFrame>
    </>
  );
}
