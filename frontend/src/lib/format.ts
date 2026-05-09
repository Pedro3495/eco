export function formatCurrency(value: number) {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL"
  }).format(value);
}

export function formatPercent(value: number) {
  return `${value.toFixed(1).replace(".", ",")}%`;
}

