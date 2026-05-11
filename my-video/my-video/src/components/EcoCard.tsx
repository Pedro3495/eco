import React from "react";

interface EcoCardProps {
  children: React.ReactNode;
  gradient?: boolean;
  wide?: boolean;
  style?: React.CSSProperties;
}

export const EcoCard: React.FC<EcoCardProps> = ({ children, gradient, wide, style }) => {
  return (
    <div
      style={{
        padding: 22,
        borderRadius: 14,
        background: gradient ? "linear-gradient(135deg, #0F8F54, #18B66B)" : "#fff",
        color: gradient ? "#fff" : "#1A1D2B",
        border: gradient ? "none" : "1px solid #E5E7EB",
        boxShadow: "0 2px 12px rgba(26, 29, 43, 0.06)",
        width: wide ? "100%" : undefined,
        ...style
      }}
    >
      {children}
    </div>
  );
};
