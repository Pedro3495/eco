"use client";

import { FormEvent, useEffect, useMemo, useRef, useState } from "react";
import { AlertCircle, Loader2, Plus, X } from "lucide-react";
import {
  Account,
  Category,
  CreateTransactionRequest,
  Transaction,
  UpdateTransactionRequest
} from "@/lib/api";

type TransactionType = "INCOME" | "EXPENSE";

interface TransactionFormState {
  description: string;
  amount: string;
  type: TransactionType;
  occurredAt: string;
  accountId: string;
  categoryId: string;
  note: string;
}

interface TransactionModalProps {
  accounts: Account[];
  categories: Category[];
  transaction?: Transaction | null;
  saving: boolean;
  error: string | null;
  onClose: () => void;
  onSubmit: (data: CreateTransactionRequest | UpdateTransactionRequest) => Promise<void>;
}

function todayIso() {
  return new Date().toISOString().slice(0, 10);
}

function categoryMatchesType(category: Category, type: TransactionType) {
  return category.kind === type || category.kind === "BOTH";
}

function buildInitialForm(
  accounts: Account[],
  categories: Category[],
  transaction?: Transaction | null
): TransactionFormState {
  const type = transaction?.type ?? "EXPENSE";

  return {
    description: transaction?.description ?? "",
    amount: transaction ? String(transaction.amount) : "",
    type,
    occurredAt: transaction?.occurredAt ?? todayIso(),
    accountId: transaction?.accountId ?? accounts[0]?.id ?? "",
    categoryId: transaction?.categoryId ?? categories.find((category) => categoryMatchesType(category, type))?.id ?? "",
    note: transaction?.note ?? ""
  };
}

export function TransactionModal({
  accounts,
  categories,
  transaction,
  saving,
  error,
  onClose,
  onSubmit
}: TransactionModalProps) {
  const [form, setForm] = useState(() => buildInitialForm(accounts, categories, transaction));
  const isEditing = Boolean(transaction);
  const modalRef = useRef<HTMLDivElement>(null);
  const firstInputRef = useRef<HTMLInputElement>(null);
  const previousActiveElement = useRef<HTMLElement | null>(null);

  useEffect(() => {
    setForm(buildInitialForm(accounts, categories, transaction));
  }, [accounts, categories, transaction]);

  useEffect(() => {
    previousActiveElement.current = document.activeElement as HTMLElement;
    document.body.style.overflow = "hidden";

    const timer = setTimeout(() => {
      firstInputRef.current?.focus();
    }, 50);

    function handleKeyDown(event: KeyboardEvent) {
      if (event.key === "Escape" && !saving) {
        onClose();
      }
      if (event.key === "Tab" && modalRef.current) {
        const focusableElements = modalRef.current.querySelectorAll<HTMLElement>(
          "button, [href], input, select, textarea, [tabindex]:not([tabindex='-1'])"
        );
        const firstElement = focusableElements[0];
        const lastElement = focusableElements[focusableElements.length - 1];

        if (event.shiftKey && document.activeElement === firstElement) {
          event.preventDefault();
          lastElement?.focus();
        } else if (!event.shiftKey && document.activeElement === lastElement) {
          event.preventDefault();
          firstElement?.focus();
        }
      }
    }

    document.addEventListener("keydown", handleKeyDown);
    return () => {
      document.removeEventListener("keydown", handleKeyDown);
      document.body.style.overflow = "";
      clearTimeout(timer);
      previousActiveElement.current?.focus();
    };
  }, [onClose, saving]);

  const filteredCategories = useMemo(
    () => categories.filter((category) => categoryMatchesType(category, form.type)),
    [categories, form.type]
  );

  function updateForm(field: keyof TransactionFormState, value: string) {
    setForm((current) => {
      const next = { ...current, [field]: value };

      if (field === "type") {
        next.categoryId = categories.find((category) => categoryMatchesType(category, value as TransactionType))?.id ?? "";
      }

      return next;
    });
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    await onSubmit({
      description: form.description.trim(),
      amount: Number(form.amount),
      type: form.type,
      occurredAt: form.occurredAt,
      accountId: form.accountId,
      categoryId: form.categoryId,
      note: form.note.trim() || undefined,
      ...(isEditing ? { active: transaction?.active ?? true } : {})
    });
  }

  return (
    <div className="modal-backdrop" role="presentation" onClick={(e) => {
      if (e.target === e.currentTarget && !saving) {
        onClose();
      }
    }}>
      <div
        className="modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="transaction-form-title"
        aria-describedby={error ? "transaction-form-error" : undefined}
        ref={modalRef}
      >
        <div className="modal-header">
          <div>
            <span className="section-label">Transação</span>
            <h2 id="transaction-form-title">{isEditing ? "Editar transação" : "Nova transação"}</h2>
          </div>
          <button className="icon-button" type="button" onClick={onClose} aria-label="Fechar modal" disabled={saving}>
            <X size={18} aria-hidden="true" />
          </button>
        </div>

        <form className="transaction-form" onSubmit={handleSubmit}>
          <label htmlFor="tx-description">
            Descrição
            <input
              id="tx-description"
              ref={firstInputRef}
              value={form.description}
              onChange={(event) => updateForm("description", event.target.value)}
              required
              maxLength={120}
              autoComplete="off"
            />
          </label>

          <div className="form-grid">
            <label htmlFor="tx-amount">
              Valor
              <input
                id="tx-amount"
                type="number"
                min="0.01"
                step="0.01"
                value={form.amount}
                onChange={(event) => updateForm("amount", event.target.value)}
                required
                inputMode="decimal"
              />
            </label>

            <label htmlFor="tx-type">
              Tipo
              <select id="tx-type" value={form.type} onChange={(event) => updateForm("type", event.target.value)}>
                <option value="EXPENSE">Despesa</option>
                <option value="INCOME">Receita</option>
              </select>
            </label>
          </div>

          <label htmlFor="tx-date">
            Data
            <input
              id="tx-date"
              type="date"
              value={form.occurredAt}
              onChange={(event) => updateForm("occurredAt", event.target.value)}
              required
            />
          </label>

          <div className="form-grid">
            <label htmlFor="tx-account">
              Conta
              <select
                id="tx-account"
                value={form.accountId}
                onChange={(event) => updateForm("accountId", event.target.value)}
                required
              >
                <option value="" disabled>Selecione</option>
                {accounts.map((account) => (
                  <option key={account.id} value={account.id}>{account.name}</option>
                ))}
              </select>
            </label>

            <label htmlFor="tx-category">
              Categoria
              <select
                id="tx-category"
                value={form.categoryId}
                onChange={(event) => updateForm("categoryId", event.target.value)}
                required
              >
                <option value="" disabled>Selecione</option>
                {filteredCategories.map((category) => (
                  <option key={category.id} value={category.id}>{category.name}</option>
                ))}
              </select>
            </label>
          </div>

          <label htmlFor="tx-note">
            Nota
            <textarea
              id="tx-note"
              value={form.note}
              onChange={(event) => updateForm("note", event.target.value)}
              rows={3}
              maxLength={255}
            />
          </label>

          {error && (
            <div className="form-error" id="transaction-form-error" role="alert">
              <AlertCircle size={16} aria-hidden="true" />
              <span>{error}</span>
            </div>
          )}

          <div className="form-actions">
            <button className="button secondary" type="button" onClick={onClose} disabled={saving}>
              Cancelar
            </button>
            <button className="button" type="submit" disabled={saving} aria-busy={saving}>
              {saving ? <Loader2 size={16} className="spin" aria-hidden="true" /> : <Plus size={16} aria-hidden="true" />}
              Salvar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
