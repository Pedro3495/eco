"use client";

import { useRouter } from "next/navigation";
import { LogOut } from "lucide-react";
import { logout } from "@/lib/api";

export function LogoutButton() {
  const router = useRouter();

  async function handleLogout() {
    await logout();
    router.replace("/login");
  }

  return (
    <button className="theme-toggle" type="button" onClick={handleLogout} aria-label="Sair">
      <LogOut size={16} aria-hidden="true" />
    </button>
  );
}
