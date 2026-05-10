"use client";

import { FormEvent, useEffect, useMemo, useState } from "react";
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

  useEffect(() => {
    setForm(buildInitialForm(accounts, categories, transaction));
  }, [accounts, categories, transaction]);

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
    <div className="modal-backdrop" role="presentation">
      <div className="modal" role="dialog" aria-modal="true" aria-labelledby="transaction-form-title">
        <div className="modal-header">
          <div>
            <span className="section-label">Transação</span>
            <h2 id="transaction-form-title">{isEditing ? "Editar transação" : "Nova transação"}</h2>
          </div>
          <button className="icon-button" type="button" onClick={onClose} aria-label="Fechar" disabled={saving}>
            <X size={18} />
          </button>
        </div>

        <form className="transaction-form" onSubmit={handleSubmit}>
          <label>
            Descrição
            <input
              value={form.description}
              onChange={(event) => updateForm("description", event.target.value)}
              required
              maxLength={120}
            />
          </label>

          <div className="form-grid">
            <label>
              Valor
              <input
                type="number"
                min="0.01"
                step="0.01"
                value={form.amount}
                onChange={(event) => updateForm("amount", event.target.value)}
                required
              />
            </label>

            <label>
              Tipo
              <select value={form.type} onChange={(event) => updateForm("type", event.target.value)}>
                <option value="EXPENSE">Despesa</option>
                <option value="INCOME">Receita</option>
              </select>
            </label>
          </div>

          <label>
            Data
            <input
              type="date"
              value={form.occurredAt}
              onChange={(event) => updateForm("occurredAt", event.target.value)}
              required
            />
          </label>

          <div className="form-grid">
            <label>
              Conta
              <select
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

            <label>
              Categoria
              <select
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

          <label>
            Nota
            <textarea
              value={form.note}
              onChange={(event) => updateForm("note", event.target.value)}
              rows={3}
              maxLength={255}
            />
          </label>

          {error && (
            <div className="form-error">
              <AlertCircle size={16} />
              <span>{error}</span>
            </div>
          )}

          <div className="form-actions">
            <button className="button secondary" type="button" onClick={onClose} disabled={saving}>
              Cancelar
            </button>
            <button className="button" type="submit" disabled={saving}>
              {saving ? <Loader2 size={16} className="spin" /> : <Plus size={16} />}
              Salvar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
