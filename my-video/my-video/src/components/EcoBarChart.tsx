import React from "react";

interface BarData {
  name: string;
  total: number;
  color: string;
}

interface EcoBarChartProps {
  data: BarData[];
  width?: number;
  height?: number;
}

export const EcoBarChart: React.FC<EcoBarChartProps> = ({ data, width = 320, height = 180 }) => {
  const max = Math.max(...data.map(d => d.total)) * 1.1;
  const barH = 20;
  const gap = 16;
  const labelW = 100;
  const chartW = width - labelW - 20;

  return (
    <svg width={width} height={height} viewBox={`0 0 ${width} ${height}`}>
      {data.map((d, i) => {
        const y = i * (barH + gap) + 10;
        const barW = (d.total / max) * chartW;
        return (
          <g key={d.name}>
            <text x={labelW - 8} y={y + barH / 1.4} textAnchor="end" fontSize={11} fill="#4B5563">{d.name}</text>
            <rect x={labelW} y={y} width={barW} height={barH} rx={6} fill={d.color} />
          </g>
        );
      })}
    </svg>
  );
};
