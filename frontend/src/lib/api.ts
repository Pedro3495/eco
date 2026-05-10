const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";

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
  accountName: string;
  categoryName: string;
}

export interface PaginatedTransactions {
  items: Transaction[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

async function fetchJson<T>(url: string): Promise<T> {
  const res = await fetch(url, { cache: "no-store" });
  if (!res.ok) {
    throw new Error(`HTTP ${res.status}: ${res.statusText}`);
  }
  return res.json();
}

export function getMonthlySummary(year: number, month: number): Promise<MonthlySummary> {
  return fetchJson<MonthlySummary>(
    `${API_BASE_URL}/reports/monthly-summary?year=${year}&month=${month}`
  );
}

export function getTransactions(page = 0, size = 10): Promise<PaginatedTransactions> {
  return fetchJson<PaginatedTransactions>(
    `${API_BASE_URL}/transactions?active=true&page=${page}&size=${size}`
  );
}
