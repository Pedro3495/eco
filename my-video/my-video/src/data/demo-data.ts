export const dashboardMonthLabel = "Maio 2026";

export const summary = {
  income: 18500,
  expense: 6050,
  result: 12450,
  creditCardTotal: 2850,
  budgetUsage: 45.2
};

export const metrics = [
  { label: "Receitas", value: 18500, variant: "positive" as const },
  { label: "Despesas", value: 6050, variant: "negative" as const },
  { label: "Resultado", value: 12450, variant: "positive" as const }
];

export const cashFlowData = [
  { month: "Jan", income: 14200, expense: 6800 },
  { month: "Fev", income: 15500, expense: 6200 },
  { month: "Mar", income: 16800, expense: 7100 },
  { month: "Abr", income: 17200, expense: 5800 },
  { month: "Mai", income: 18500, expense: 6050 }
];

export const categorySpending = [
  { name: "Alimentação", total: 1850, color: "#E86F51" },
  { name: "Transporte", total: 920, color: "#2A9D8F" },
  { name: "Moradia", total: 2200, color: "#264653" },
  { name: "Lazer", total: 680, color: "#F4A261" },
  { name: "Assinaturas", total: 240, color: "#8AB17D" },
  { name: "Saúde", total: 160, color: "#E76F51" }
];

export const transactions = [
  { id: "1", description: "Salário Mensal", category: "Receita", account: "Conta Principal", type: "INCOME" as const, amount: 18500, date: "2026-05-01" },
  { id: "2", description: "Aluguel", category: "Moradia", account: "Conta Principal", type: "EXPENSE" as const, amount: 2200, date: "2026-05-03" },
  { id: "3", description: "Supermercado", category: "Alimentação", account: "Cartão Crédito", type: "EXPENSE" as const, amount: 485, date: "2026-05-08" },
  { id: "4", description: "Freela Design", category: "Receita", account: "Conta Principal", type: "INCOME" as const, amount: 3500, date: "2026-05-12" },
  { id: "5", description: "Uber & Combustível", category: "Transporte", account: "Cartão Crédito", type: "EXPENSE" as const, amount: 320, date: "2026-05-15" }
];

export const budgets = [
  { category: "Alimentação", limit: 2500, spent: 1850 },
  { category: "Transporte", limit: 1200, spent: 920 },
  { category: "Lazer", limit: 1000, spent: 680 },
  { category: "Assinaturas", limit: 400, spent: 240 },
  { category: "Saúde", limit: 500, spent: 160 }
];

export const goals = [
  { id: "g1", name: "Reserva de Emergência", current: 15000, target: 20000 },
  { id: "g2", name: "Carro Novo", current: 36000, target: 60000 },
  { id: "g3", name: "Viagem Europa", current: 8000, target: 8000 }
];

export function formatCurrency(value: number) {
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(value);
}
