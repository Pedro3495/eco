"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { AlertCircle, Loader2, LockKeyhole, LogIn } from "lucide-react";
import { ApiError, login, saveAuthTokens } from "@/lib/api";
import { ThemeToggle } from "@/components/ThemeToggle";

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("dev@eco.com");
  const [password, setPassword] = useState("123456");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await login({ email, password });
      saveAuthTokens(response);
      router.replace("/");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Nao foi possivel entrar.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-shell">
      <section className="login-panel" aria-labelledby="login-title">
        <div className="login-topbar">
          <div className="brand">
            <div className="brand-mark" aria-hidden="true">E</div>
            <div>
              <p className="eyebrow">Eco Financas</p>
              <strong>Entrar</strong>
            </div>
          </div>
          <ThemeToggle />
        </div>

        <div className="login-copy">
          <div className="login-icon" aria-hidden="true">
            <LockKeyhole size={22} />
          </div>
          <h1 id="login-title">Acesse sua carteira</h1>
          <p>Use o usuario dev local para testar o fluxo JWT do backend.</p>
        </div>

        <form className="login-form" onSubmit={handleSubmit}>
          <label htmlFor="login-email">
            Email
            <input
              id="login-email"
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              autoComplete="email"
              required
            />
          </label>

          <label htmlFor="login-password">
            Senha
            <input
              id="login-password"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              autoComplete="current-password"
              required
            />
          </label>

          {error && (
            <div className="form-error" role="alert">
              <AlertCircle size={16} aria-hidden="true" />
              <span>{error}</span>
            </div>
          )}

          <button className="button primary lg" type="submit" disabled={loading} aria-busy={loading}>
            {loading ? <Loader2 size={18} className="spin" aria-hidden="true" /> : <LogIn size={18} aria-hidden="true" />}
            Entrar
          </button>
        </form>
      </section>
    </main>
  );
}
