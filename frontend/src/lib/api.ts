const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";
const REQUEST_TIMEOUT_MS = 5000;
const ACCESS_TOKEN_KEY = "eco-access-token";
const REFRESH_TOKEN_KEY = "eco-refresh-token";

export interface MonthlySummary {
  income: number;
  expense: number;
  balance: number;
}

export interface DashboardBudget {
  generalLimit: number | null;
  spentAmount: number;
  usagePercent: number;
}

export interface DashboardGoal {
  id: string;
  name: string;
  progressPercent: number;
}

export interface DashboardMonthly {
  month: string;
  income: number;
  expense: number;
  result: number;
  creditCardTotal: number;
  budget: DashboardBudget;
  goals: DashboardGoal[];
}

export interface DashboardCategory {
  categoryId: string;
  categoryName: string;
  total: number;
  percentage: number;
}

export interface DashboardCashFlow {
  month: string;
  income: number;
  expense: number;
  result: number;
}

export interface Transaction {
  id: string;
  description: string;
  amount: number;
  type: "INCOME" | "EXPENSE";
  occurredAt: string;
  accountId?: string;
  accountName: string;
  categoryId?: string;
  categoryName: string;
  note?: string | null;
  active?: boolean;
}

export interface PaginatedTransactions {
  items: Transaction[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

export interface Account {
  id: string;
  name: string;
  type?: "CHECKING" | "CASH" | "CREDIT_CARD" | "INVESTMENT";
  initialBalance?: number;
  currentBalance?: number;
  currency?: string;
  active?: boolean;
}

export interface Category {
  id: string;
  name: string;
  kind: "INCOME" | "EXPENSE" | "BOTH";
  color?: string;
  icon?: string;
  active?: boolean;
}

export interface BudgetCategorySummary {
  categoryId: string;
  categoryName: string;
  limitAmount: number;
  spentAmount: number;
  usagePercent: number;
}

export interface BudgetSummary {
  month: string;
  generalLimit: number | null;
  totalSpent: number;
  generalUsagePercent: number;
  categories: BudgetCategorySummary[];
}

export interface Goal {
  id: string;
  name: string;
  targetAmount: number;
  currentAmount: number;
  targetDate?: string | null;
  status: "ACTIVE" | "COMPLETED" | "ARCHIVED";
  progressPercent: number;
}

export interface AuthUser {
  id: string;
  name: string;
  email: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: "Bearer";
  expiresIn: number;
  user: AuthUser;
}

export interface LoginRequest {
  email: string;
  password: string;
}

interface RawCategory extends Omit<Category, "kind"> {
  kind?: Category["kind"];
  type?: Category["kind"];
}

export interface CreateTransactionRequest {
  description: string;
  amount: number;
  type: "INCOME" | "EXPENSE";
  occurredAt: string;
  accountId: string;
  categoryId: string;
  note?: string;
}

export interface UpdateTransactionRequest extends CreateTransactionRequest {
  active: boolean;
}

export interface TransactionFilters {
  page?: number;
  size?: number;
  startDate?: string;
  endDate?: string;
  type?: "INCOME" | "EXPENSE";
  accountId?: string;
  categoryId?: string;
  active?: boolean;
}

interface ApiErrorBody {
  message?: string;
  fields?: Record<string, string>;
}

export class ApiError extends Error {
  status: number;
  fields?: Record<string, string>;

  constructor(status: number, message: string, fields?: Record<string, string>) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.fields = fields;
  }
}

async function fetchJson<T>(url: string, options?: RequestInit, retryOnUnauthorized = true): Promise<T> {
  const controller = new AbortController();
  const timeout = window.setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);
  const headers = new Headers(options?.headers);
  const token = getAccessToken();

  if (token && !headers.has("Authorization")) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  let res: Response;

  try {
    res = await fetch(url, { cache: "no-store", ...options, headers, signal: controller.signal });
  } finally {
    window.clearTimeout(timeout);
  }

  if (res.status === 401 && retryOnUnauthorized && !url.includes("/auth/")) {
    try {
      const refreshed = await refreshAuthToken();
      const retryHeaders = new Headers(options?.headers);
      retryHeaders.set("Authorization", `Bearer ${refreshed.accessToken}`);
      return fetchJson<T>(url, { ...options, headers: retryHeaders }, false);
    } catch {
      clearAuthTokens();
      if (typeof window !== "undefined" && window.location.pathname !== "/login") {
        window.location.href = "/login";
      }
    }
  }

  if (res.status === 401) {
    clearAuthTokens();
    if (typeof window !== "undefined" && window.location.pathname !== "/login") {
      window.location.href = "/login";
    }
  }

  if (!res.ok) {
    let errorBody: ApiErrorBody | null = null;

    try {
      errorBody = await res.json();
    } catch {
      errorBody = null;
    }

    const fieldMessages = errorBody?.fields ? Object.values(errorBody.fields).join(" ") : "";
    const message = [errorBody?.message, fieldMessages].filter(Boolean).join(" ") || `HTTP ${res.status}: ${res.statusText}`;
    throw new ApiError(res.status, message, errorBody?.fields);
  }

  if (res.status === 204) {
    return undefined as T;
  }

  return res.json();
}

let refreshPromise: Promise<AuthResponse> | null = null;

async function refreshAuthToken(): Promise<AuthResponse> {
  const refreshToken = getRefreshToken();

  if (!refreshToken) {
    throw new ApiError(401, "Sessao expirada");
  }

  if (!refreshPromise) {
    refreshPromise = fetchJson<AuthResponse>(`${API_BASE_URL}/auth/refresh`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ refreshToken }),
    }, false).finally(() => {
      refreshPromise = null;
    });
  }

  const response = await refreshPromise;
  saveAuthTokens(response);
  return response;
}

export function getAccessToken() {
  if (typeof window === "undefined") return null;
  return window.localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function getRefreshToken() {
  if (typeof window === "undefined") return null;
  return window.localStorage.getItem(REFRESH_TOKEN_KEY);
}

export function saveAuthTokens(response: AuthResponse) {
  window.localStorage.setItem(ACCESS_TOKEN_KEY, response.accessToken);
  window.localStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken);
}

export function clearAuthTokens() {
  if (typeof window === "undefined") return;
  window.localStorage.removeItem(ACCESS_TOKEN_KEY);
  window.localStorage.removeItem(REFRESH_TOKEN_KEY);
}

export function isAuthenticated() {
  return Boolean(getAccessToken());
}

export function login(data: LoginRequest): Promise<AuthResponse> {
  return fetchJson<AuthResponse>(`${API_BASE_URL}/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });
}

export function logout(): Promise<void> {
  const refreshToken = getRefreshToken();
  clearAuthTokens();

  if (!refreshToken) {
    return Promise.resolve();
  }

  return fetchJson<void>(`${API_BASE_URL}/auth/logout`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ refreshToken }),
  }).catch(() => undefined);
}

export function getMe(): Promise<AuthUser> {
  return fetchJson<AuthUser>(`${API_BASE_URL}/auth/me`);
}

function toQueryString(params: Record<string, string | number | boolean | undefined>) {
  const searchParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== "") {
      searchParams.set(key, String(value));
    }
  });

  return searchParams.toString();
}

export function createTransaction(
  data: CreateTransactionRequest
): Promise<Transaction> {
  return fetchJson<Transaction>(`${API_BASE_URL}/transactions`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });
}

export function updateTransaction(
  id: string,
  data: UpdateTransactionRequest
): Promise<Transaction> {
  return fetchJson<Transaction>(`${API_BASE_URL}/transactions/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });
}

export function deleteTransaction(id: string): Promise<void> {
  return fetchJson<void>(`${API_BASE_URL}/transactions/${id}`, {
    method: "DELETE",
  });
}

export function getMonthlySummary(year: number, month: number): Promise<MonthlySummary> {
  return fetchJson<MonthlySummary>(
    `${API_BASE_URL}/reports/monthly-summary?year=${year}&month=${month}`
  );
}

export function getTransactions(filters: TransactionFilters = {}): Promise<PaginatedTransactions> {
  const query = toQueryString({
    active: filters.active ?? true,
    page: filters.page ?? 0,
    size: filters.size ?? 10,
    startDate: filters.startDate,
    endDate: filters.endDate,
    type: filters.type,
    accountId: filters.accountId,
    categoryId: filters.categoryId,
  });

  return fetchJson<PaginatedTransactions>(`${API_BASE_URL}/transactions?${query}`);
}

export function getAccounts(): Promise<Account[]> {
  return fetchJson<Account[]>(`${API_BASE_URL}/accounts`);
}

export function createAccount(data: {
  name: string;
  type: NonNullable<Account["type"]>;
  initialBalance: number;
}): Promise<Account> {
  return fetchJson<Account>(`${API_BASE_URL}/accounts`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export function getCategories(): Promise<Category[]> {
  return fetchJson<RawCategory[]>(`${API_BASE_URL}/categories`).then((categories) =>
    categories.map((category) => ({
      ...category,
      kind: category.kind ?? category.type ?? "BOTH",
    }))
  );
}

export function createCategory(data: {
  name: string;
  kind: Category["kind"];
  color?: string;
  icon?: string;
}): Promise<Category> {
  return fetchJson<RawCategory>(`${API_BASE_URL}/categories`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  }).then((category) => ({
    ...category,
    kind: category.kind ?? category.type ?? "BOTH",
  }));
}

export function getDashboardMonthly(month: string): Promise<DashboardMonthly> {
  return fetchJson<DashboardMonthly>(`${API_BASE_URL}/dashboard/monthly?month=${month}`);
}

export function getDashboardCategories(month: string): Promise<DashboardCategory[]> {
  return fetchJson<DashboardCategory[]>(`${API_BASE_URL}/dashboard/categories?month=${month}`);
}

export function getDashboardCashFlow(from: string, to: string): Promise<DashboardCashFlow[]> {
  return fetchJson<DashboardCashFlow[]>(`${API_BASE_URL}/dashboard/cash-flow?from=${from}&to=${to}`);
}

export function getBudgetSummary(month: string): Promise<BudgetSummary> {
  return fetchJson<BudgetSummary>(`${API_BASE_URL}/budgets/${month}/summary`);
}

export function upsertMonthlyBudget(month: string, generalLimit: number): Promise<unknown> {
  return fetchJson(`${API_BASE_URL}/budgets/${month}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ generalLimit }),
  });
}

export function upsertCategoryBudget(month: string, categoryId: string, limitAmount: number): Promise<unknown> {
  return fetchJson(`${API_BASE_URL}/budgets/${month}/categories/${categoryId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ limitAmount }),
  });
}

export function getGoals(): Promise<Goal[]> {
  return fetchJson<Goal[]>(`${API_BASE_URL}/goals`);
}

export function createGoal(data: {
  name: string;
  targetAmount: number;
  currentAmount: number;
  targetDate?: string | null;
}): Promise<Goal> {
  return fetchJson<Goal>(`${API_BASE_URL}/goals`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export function updateGoalProgress(id: string, currentAmount: number): Promise<Goal> {
  return fetchJson<Goal>(`${API_BASE_URL}/goals/${id}/progress`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ currentAmount }),
  });
}
