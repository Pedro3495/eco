"use client";

import { usePathname } from "next/navigation";
import Link from "next/link";
import { Home, ReceiptText, Wallet, Tag, Target, LogOut } from "lucide-react";
import { logout } from "@/lib/api";

const navItems = [
  { href: "/", label: "Início", icon: Home },
  { href: "/transactions", label: "Movimentos", icon: ReceiptText },
  { href: "/accounts", label: "Contas", icon: Wallet },
  { href: "/budgets", label: "Orçamento", icon: Tag },
  { href: "/goals", label: "Metas", icon: Target }
];

export function BottomNav() {
  const pathname = usePathname();

  async function handleLogout() {
    await logout();
    window.location.href = "/login";
  }

  return (
    <nav className="bottom-nav" aria-label="Navegação principal">
      {navItems.map((item) => {
        const isActive = pathname === item.href || pathname.startsWith(`${item.href}/`);
        const Icon = item.icon;
        return (
          <Link
            key={item.href}
            href={item.href}
            className={`bottom-nav-item ${isActive ? "active" : ""}`}
            aria-current={isActive ? "page" : undefined}
          >
            <Icon size={20} aria-hidden="true" strokeWidth={isActive ? 2.5 : 2} />
            <span>{item.label}</span>
          </Link>
        );
      })}
      <button className="bottom-nav-item" type="button" onClick={handleLogout} aria-label="Sair">
        <LogOut size={20} aria-hidden="true" />
        <span>Sair</span>
      </button>
    </nav>
  );
}
