"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import type { ReactNode } from "react";
import {
  BarChart3,
  CalendarCheck,
  CreditCard,
  LayoutDashboard,
  Leaf,
  List,
  Settings,
  Target,
  UserRound,
  WalletCards
} from "lucide-react";
import { BottomNav } from "./BottomNav";
import { LogoutButton } from "./LogoutButton";
import { ThemeToggle } from "./ThemeToggle";

const navItems = [
  { href: "/", label: "Dashboard", icon: LayoutDashboard },
  { href: "/transactions", label: "Transacoes", icon: List },
  { href: "/budgets", label: "Orcamentos", icon: CalendarCheck },
  { href: "/goals", label: "Metas", icon: Target },
  { href: "/accounts", label: "Contas", icon: CreditCard },
  { href: "/categories", label: "Categorias", icon: BarChart3 }
];

interface AppFrameProps {
  title: string;
  eyebrow?: string;
  children: ReactNode;
  actions?: ReactNode;
}

export function AppFrame({ title, eyebrow, children, actions }: AppFrameProps) {
  const pathname = usePathname();

  return (
    <>
      <div className="app-frame">
        <aside className="sidebar" aria-label="Navegacao principal">
          <Link href="/" className="sidebar-brand" aria-label="Eco Dashboard">
            <Leaf size={26} aria-hidden="true" />
            <span>Eco</span>
          </Link>

          <nav className="sidebar-nav">
            {navItems.map((item) => {
              const Icon = item.icon;
              const active = pathname === item.href;
              return (
                <Link key={item.href} href={item.href} className={`sidebar-link ${active ? "active" : ""}`}>
                  <Icon size={19} aria-hidden="true" />
                  <span>{item.label}</span>
                </Link>
              );
            })}
          </nav>

          <div className="sidebar-footer">
            <div className="sidebar-link muted-link">
              <WalletCards size={19} aria-hidden="true" />
              <span>Investimentos</span>
            </div>
            <div className="sidebar-link muted-link">
              <Settings size={19} aria-hidden="true" />
              <span>Configuracoes</span>
            </div>
          </div>
        </aside>

        <main className="app-main">
          <header className="app-topbar">
            <div>
              {eyebrow && <p className="eyebrow">{eyebrow}</p>}
              <h1>{title}</h1>
            </div>
            <div className="app-actions">
              {actions}
              <ThemeToggle />
              <div className="user-chip" aria-label="Usuario atual">
                <span className="avatar"><UserRound size={17} aria-hidden="true" /></span>
                <span>Ola, Ana</span>
              </div>
              <LogoutButton />
            </div>
          </header>

          {children}
        </main>
      </div>
      <BottomNav />
    </>
  );
}
