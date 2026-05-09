export const monthlySummary = {
  month: "Maio 2026",
  income: 5000,
  expense: 2750,
  result: 2250,
  creditCardTotal: 1320,
  budgetUsage: 68.75
};

export const categorySpending = [
  { name: "Alimentacao", total: 950, color: "#E86F51" },
  { name: "Transporte", total: 420, color: "#2A9D8F" },
  { name: "Moradia", total: 780, color: "#264653" },
  { name: "Lazer", total: 300, color: "#F4A261" },
  { name: "Assinaturas", total: 120, color: "#8AB17D" }
];

export const transactions = [
  {
    id: "1",
    description: "Mercado semanal",
    merchantName: "Supermercado X",
    category: "Alimentacao",
    account: "Conta Principal",
    type: "EXPENSE",
    amount: 185.9,
    date: "2026-05-05"
  },
  {
    id: "2",
    description: "Salario",
    merchantName: "Empresa",
    category: "Receita",
    account: "Conta Principal",
    type: "INCOME",
    amount: 5000,
    date: "2026-05-01"
  },
  {
    id: "3",
    description: "Restaurante",
    merchantName: "Restaurante Y",
    category: "Alimentacao",
    account: "Cartao de Credito",
    type: "EXPENSE",
    amount: 120,
    date: "2026-05-20",
    billingMonth: "2026-06"
  }
];

export const budgets = [
  { category: "Alimentacao", limit: 1200, spent: 950 },
  { category: "Transporte", limit: 500, spent: 420 },
  { category: "Lazer", limit: 400, spent: 300 }
];

export const goals = [
  {
    name: "Reserva de emergencia",
    targetAmount: 10000,
    currentAmount: 2500
  },
  {
    name: "Notebook novo",
    targetAmount: 6000,
    currentAmount: 1800
  }
];

