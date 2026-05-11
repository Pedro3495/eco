"use client";

import { useEffect, useState } from "react";
import { Moon, Sun } from "lucide-react";

export function ThemeToggle() {
  const [theme, setTheme] = useState<"light" | "dark">("light");
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
    const saved = localStorage.getItem("eco-theme") as "light" | "dark" | null;
    const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
    const initial = saved ?? (prefersDark ? "dark" : "light");
    setTheme(initial);
    document.documentElement.setAttribute("data-theme", initial);
  }, []);

  function toggle() {
    const next = theme === "light" ? "dark" : "light";
    setTheme(next);
    document.documentElement.setAttribute("data-theme", next);
    localStorage.setItem("eco-theme", next);
  }

  if (!mounted) {
    return (
      <button className="theme-toggle" aria-label="Alternar tema" type="button" disabled>
        <Sun size={16} aria-hidden="true" />
      </button>
    );
  }

  return (
    <button
      className="theme-toggle"
      aria-label={theme === "light" ? "Ativar modo escuro" : "Ativar modo claro"}
      type="button"
      onClick={toggle}
    >
      {theme === "light" ? <Moon size={16} aria-hidden="true" /> : <Sun size={16} aria-hidden="true" />}
    </button>
  );
}
