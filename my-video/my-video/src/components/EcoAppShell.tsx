import React from "react";
import { EcoSidebar } from "./EcoSidebar";
import { EcoTopbar } from "./EcoTopbar";

interface Props {
  children: React.ReactNode;
  monthLabel: string;
  dark?: boolean;
}

export const EcoAppShell: React.FC<Props> = ({ children, monthLabel, dark }) => {
  return (
    <div style={{
      display: "flex", width: "100%", height: "100%",
      background: dark ? "#0D1512" : "#F5F7F6",
      color: dark ? "#F3F4F8" : "#1A1D2B",
      fontFamily: "'Inter', system-ui, sans-serif",
      overflow: "hidden"
    }}>
      <EcoSidebar dark={dark} />
      <div style={{ flex: 1, padding: "28px 36px", overflow: "hidden", display: "flex", flexDirection: "column" }}>
        <EcoTopbar monthLabel={monthLabel} dark={dark} />
        <div style={{ flex: 1, overflow: "hidden" }}>
          {children}
        </div>
      </div>
    </div>
  );
};
