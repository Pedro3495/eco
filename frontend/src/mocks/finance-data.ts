export const monthlySummary = {
  month: "Maio 2026",
  income: 5000,
  expense: 2750,
  result: 2250,
  creditCardTotal: 1320,
  budgetUsage: 68.75
};

export const categorySpending = [
  { name: "Alimentação", total: 950, color: "#E86F51", icon: "utensils" },
  { name: "Transporte", total: 420, color: "#2A9D8F", icon: "car" },
  { name: "Moradia", total: 780, color: "#264653", icon: "home" },
  { name: "Lazer", total: 300, color: "#F4A261", icon: "gamepad-2" },
  { name: "Assinaturas", total: 120, color: "#8AB17D", icon: "monitor" },
  { name: "Saúde", total: 180, color: "#E76F51", icon: "heart-pulse" }
];

export const transactions = [
  {
    id: "1",
    description: "Mercado semanal",
    merchantName: "Supermercado X",
    category: "Alimentação",
    account: "Conta Principal",
    type: "EXPENSE",
    amount: 185.9,
    date: "2026-05-05"
  },
  {
    id: "2",
    description: "Salário",
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
    category: "Alimentação",
    account: "Cartão de Crédito",
    type: "EXPENSE",
    amount: 120,
    date: "2026-05-20",
    billingMonth: "2026-06"
  },
  {
    id: "4",
    description: "Uber",
    merchantName: "Uber",
    category: "Transporte",
    account: "Cartão de Crédito",
    type: "EXPENSE",
    amount: 28.5,
    date: "2026-05-18"
  },
  {
    id: "5",
    description: "Netflix",
    merchantName: "Netflix",
    category: "Assinaturas",
    account: "Conta Principal",
    type: "EXPENSE",
    amount: 39.9,
    date: "2026-05-10"
  },
  {
    id: "6",
    description: "Farmácia",
    merchantName: "Drogaria Z",
    category: "Saúde",
    account: "Conta Principal",
    type: "EXPENSE",
    amount: 89.5,
    date: "2026-05-15"
  }
];

export const budgets = [
  { category: "Alimentação", limit: 1200, spent: 950 },
  { category: "Transporte", limit: 500, spent: 420 },
  { category: "Lazer", limit: 400, spent: 300 },
  { category: "Assinaturas", limit: 200, spent: 120 },
  { category: "Saúde", limit: 300, spent: 180 }
];

export const goals = [
  {
    id: "g1",
    name: "Reserva de emergência",
    targetAmount: 10000,
    currentAmount: 2500,
    targetDate: "2026-12-31",
    status: "ACTIVE"
  },
  {
    id: "g2",
    name: "Notebook novo",
    targetAmount: 6000,
    currentAmount: 1800,
    targetDate: "2026-08-31",
    status: "ACTIVE"
  },
  {
    id: "g3",
    name: "Viagem de férias",
    targetAmount: 4000,
    currentAmount: 4000,
    targetDate: "2026-07-31",
    status: "COMPLETED"
  }
];

export const accounts = [
  {
    id: "a1",
    name: "Conta Principal",
    type: "CHECKING",
    currentBalance: 3250.75,
    initialBalance: 1000,
    currency: "BRL",
    active: true
  },
  {
    id: "a2",
    name: "Cartão de Crédito",
    type: "CREDIT_CARD",
    currentBalance: -1320,
    initialBalance: 0,
    currency: "BRL",
    active: true
  },
  {
    id: "a3",
    name: "Investimentos",
    type: "INVESTMENT",
    currentBalance: 5200,
    initialBalance: 2000,
    currency: "BRL",
    active: true
  },
  {
    id: "a4",
    name: "Dinheiro",
    type: "CASH",
    currentBalance: 150,
    initialBalance: 0,
    currency: "BRL",
    active: true
  }
];

export const categories = [
  { id: "c1", name: "Alimentação", kind: "EXPENSE", color: "#E86F51", icon: "utensils", active: true },
  { id: "c2", name: "Transporte", kind: "EXPENSE", color: "#2A9D8F", icon: "car", active: true },
  { id: "c3", name: "Moradia", kind: "EXPENSE", color: "#264653", icon: "home", active: true },
  { id: "c4", name: "Lazer", kind: "EXPENSE", color: "#F4A261", icon: "gamepad-2", active: true },
  { id: "c5", name: "Assinaturas", kind: "EXPENSE", color: "#8AB17D", icon: "monitor", active: true },
  { id: "c6", name: "Saúde", kind: "EXPENSE", color: "#E76F51", icon: "heart-pulse", active: true },
  { id: "c7", name: "Receita", kind: "INCOME", color: "#0ECD74", icon: "trending-up", active: true },
  { id: "c8", name: "Investimentos", kind: "BOTH", color: "#5B42C5", icon: "trending-up", active: true },
  { id: "c9", name: "Outros", kind: "BOTH", color: "#9CA3AF", icon: "more-horizontal", active: true }
];

export const cashFlowData = [
  { month: "Jan", income: 5000, expense: 3200, result: 1800 },
  { month: "Fev", income: 5000, expense: 2800, result: 2200 },
  { month: "Mar", income: 5200, expense: 3100, result: 2100 },
  { month: "Abr", income: 5000, expense: 2900, result: 2100 },
  { month: "Mai", income: 5000, expense: 2750, result: 2250 }
];
