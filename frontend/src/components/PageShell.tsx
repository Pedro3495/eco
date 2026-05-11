"use client";

import type { ReactNode } from "react";
import { BottomNav } from "./BottomNav";

interface PageShellProps {
  children: ReactNode;
  className?: string;
}

export function PageShell({ children, className = "" }: PageShellProps) {
  return (
    <>
      <main className={`shell ${className}`}>{children}</main>
      <BottomNav />
    </>
  );
}
