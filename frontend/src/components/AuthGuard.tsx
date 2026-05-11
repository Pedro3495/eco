"use client";

import { useEffect, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import { Loader2 } from "lucide-react";
import { isAuthenticated } from "@/lib/api";

interface AuthGuardProps {
  children: React.ReactNode;
}

export function AuthGuard({ children }: AuthGuardProps) {
  const pathname = usePathname();
  const router = useRouter();
  const [allowed, setAllowed] = useState(false);

  useEffect(() => {
    if (pathname === "/login") {
      setAllowed(true);
      return;
    }

    if (!isAuthenticated()) {
      router.replace("/login");
      return;
    }

    setAllowed(true);
  }, [pathname, router]);

  if (!allowed) {
    return (
      <main className="shell auth-loading" aria-busy="true" aria-live="polite">
        <Loader2 size={22} className="spin" aria-hidden="true" />
        <span>Carregando...</span>
      </main>
    );
  }

  return children;
}
