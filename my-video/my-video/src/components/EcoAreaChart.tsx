import React from "react";

interface DataPoint {
  month: string;
  income: number;
  expense: number;
}

interface EcoAreaChartProps {
  data: DataPoint[];
  width?: number;
  height?: number;
}

export const EcoAreaChart: React.FC<EcoAreaChartProps> = ({ data, width = 500, height = 200 }) => {
  const maxVal = Math.max(...data.map(d => Math.max(d.income, d.expense))) * 1.1;
  const padLeft = 40, padBottom = 30;
  const chartW = width - padLeft;
  const chartH = height - padBottom;

  const x = (i: number) => padLeft + (i / (data.length - 1)) * chartW;
  const y = (v: number) => chartH - (v / maxVal) * chartH;

  const areaPath = (key: "income" | "expense") => {
    const points = data.map((d, i) => `${x(i)},${y(d[key])}`).join(" L ");
    return `M ${x(0)},${chartH} L ${points} L ${x(data.length - 1)},${chartH} Z`;
  };

  const linePath = (key: "income" | "expense") => {
    return "M " + data.map((d, i) => `${x(i)},${y(d[key])}`).join(" L ");
  };

  return (
    <svg width={width} height={height} viewBox={`0 0 ${width} ${height}`}>
      <defs>
        <linearGradient id="gradIncome" x1="0" y1="0" x2="0" y2="1">
          <stop offset="5%" stopColor="#0ECD74" stopOpacity={0.25}/>
          <stop offset="95%" stopColor="#0ECD74" stopOpacity={0}/>
        </linearGradient>
        <linearGradient id="gradExpense" x1="0" y1="0" x2="0" y2="1">
          <stop offset="5%" stopColor="#E85D5D" stopOpacity={0.25}/>
          <stop offset="95%" stopColor="#E85D5D" stopOpacity={0}/>
        </linearGradient>
      </defs>
      {[0, 0.25, 0.5, 0.75, 1].map(t => (
        <line key={t} x1={padLeft} y1={t * chartH} x2={width} y2={t * chartH} stroke="#E5E7EB" strokeDasharray="3 3" />
      ))}
      <path d={areaPath("income")} fill="url(#gradIncome)" />
      <path d={areaPath("expense")} fill="url(#gradExpense)" />
      <path d={linePath("income")} fill="none" stroke="#0ECD74" strokeWidth={2} />
      <path d={linePath("expense")} fill="none" stroke="#E85D5D" strokeWidth={2} />
      {data.map((d, i) => (
        <text key={d.month} x={x(i)} y={height - 8} textAnchor="middle" fontSize={11} fill="#6B7280">{d.month}</text>
      ))}
    </svg>
  );
};
