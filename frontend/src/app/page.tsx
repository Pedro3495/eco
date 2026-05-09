import { ArrowUpRight, Plus } from "lucide-react";
import { budgets, categorySpending, goals, monthlySummary, transactions } from "@/mocks/finance-data";
import { formatCurrency, formatPercent } from "@/lib/format";

export default function Home() {
  return (
    <main className="shell">
      <header className="topbar">
        <div className="brand">
          <div className="brand-mark">E</div>
          <div>
            <p className="eyebrow">Eco Financas</p>
            <strong>PWA mockado</strong>
          </div>
        </div>
        <button className="button secondary">Login visual</button>
      </header>

      <section className="hero">
        <div className="panel hero-copy">
          <p className="eyebrow">MVP pessoal</p>
          <h1 className="title">Seu dinheiro, sem neblina.</h1>
          <p className="subtitle">
            Dashboard mockado para validar a experiencia antes do backend Spring Boot ficar pronto.
            Os dados abaixo sao falsos e serao trocados pela API Java.
          </p>
          <div className="cta-row">
            <button className="button">
              <Plus size={18} /> Nova transacao
            </button>
            <button className="button secondary">
              Ver contrato da API <ArrowUpRight size={16} />
            </button>
          </div>
        </div>

        <aside className="panel summary-card">
          <div>
            <span className="section-label">{monthlySummary.month}</span>
            <h2>Resumo mensal</h2>
          </div>
          <div className="metric-grid">
            <div className="metric">
              <span>Receitas</span>
              <strong className="positive">{formatCurrency(monthlySummary.income)}</strong>
            </div>
            <div className="metric">
              <span>Despesas</span>
              <strong className="negative">{formatCurrency(monthlySummary.expense)}</strong>
            </div>
            <div className="metric">
              <span>Resultado</span>
              <strong>{formatCurrency(monthlySummary.result)}</strong>
            </div>
            <div className="metric">
              <span>Cartao</span>
              <strong>{formatCurrency(monthlySummary.creditCardTotal)}</strong>
            </div>
          </div>
          <div>
            <div className="row">
              <span className="muted">Orcamento usado</span>
              <strong>{formatPercent(monthlySummary.budgetUsage)}</strong>
            </div>
            <div className="bar">
              <span style={{ width: `${monthlySummary.budgetUsage}%` }} />
            </div>
          </div>
        </aside>
      </section>

      <section className="content-grid">
        <div className="panel card">
          <span className="section-label">Categorias</span>
          <h2>Onde o dinheiro foi</h2>
          <div className="list">
            {categorySpending.map((category) => (
              <div className="row" key={category.name}>
                <div>
                  <strong>{category.name}</strong>
                  <p className="muted">{formatCurrency(category.total)} no mes</p>
                </div>
                <span style={{ color: category.color }}>●</span>
              </div>
            ))}
          </div>
        </div>

        <div className="panel card">
          <span className="section-label">Transacoes</span>
          <h2>Ultimos movimentos</h2>
          <div className="list">
            {transactions.map((transaction) => (
              <div className="row" key={transaction.id}>
                <div>
                  <strong>{transaction.description}</strong>
                  <p className="muted">
                    {transaction.category} · {transaction.account}
                  </p>
                </div>
                <strong className={transaction.type === "INCOME" ? "positive" : "negative"}>
                  {transaction.type === "INCOME" ? "+" : "-"}
                  {formatCurrency(transaction.amount)}
                </strong>
              </div>
            ))}
          </div>
        </div>

        <div className="panel card">
          <span className="section-label">Orcamento</span>
          <h2>Limites do mes</h2>
          <div className="list">
            {budgets.map((budget) => {
              const usage = Math.min((budget.spent / budget.limit) * 100, 100);
              return (
                <div key={budget.category}>
                  <div className="row">
                    <strong>{budget.category}</strong>
                    <span className="muted">
                      {formatCurrency(budget.spent)} / {formatCurrency(budget.limit)}
                    </span>
                  </div>
                  <div className="bar">
                    <span style={{ width: `${usage}%` }} />
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        <div className="panel card">
          <span className="section-label">Metas</span>
          <h2>Progresso</h2>
          <div className="list">
            {goals.map((goal) => {
              const usage = Math.min((goal.currentAmount / goal.targetAmount) * 100, 100);
              return (
                <div key={goal.name}>
                  <div className="row">
                    <strong>{goal.name}</strong>
                    <span className="muted">{formatPercent(usage)}</span>
                  </div>
                  <div className="bar">
                    <span style={{ width: `${usage}%` }} />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </section>
    </main>
  );
}

