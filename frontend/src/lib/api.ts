const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";
const REQUEST_TIMEOUT_MS = 5000;

export interface MonthlySummary {
  income: number;
  expense: number;
  balance: number;
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
  active?: boolean;
}

export interface Category {
  id: string;
  name: string;
  kind: "INCOME" | "EXPENSE" | "BOTH";
  active?: boolean;
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

async function fetchJson<T>(url: string, options?: RequestInit): Promise<T> {
  const controller = new AbortController();
  const timeout = window.setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);

  let res: Response;

  try {
    res = await fetch(url, { cache: "no-store", ...options, signal: controller.signal });
  } finally {
    window.clearTimeout(timeout);
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

export function getCategories(): Promise<Category[]> {
  return fetchJson<RawCategory[]>(`${API_BASE_URL}/categories`).then((categories) =>
    categories.map((category) => ({
      ...category,
      kind: category.kind ?? category.type ?? "BOTH",
    }))
  );
}
