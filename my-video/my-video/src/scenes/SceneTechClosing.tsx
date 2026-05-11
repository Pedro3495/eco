import React from "react";
import { useCurrentFrame, interpolate } from "remotion";

const codeLines = [
  "@RestController",
  "@RequestMapping(\"/api\")",
  "public class DashboardController {",
  "  @GetMapping(\"/dashboard\")",
  "  public Dashboard getMonthly() {",
  "    return service.summary(month);",
  "  }",
  "}",
  "",
  "// Next.js App Router",
  "export default function Page() {",
  "  const data = await getDashboard();",
  "  return <Dashboard data={data} />;",
  "}"
];

const serverIcons = ["🖥", "⚙", "🔒", "🌐", "📡"];

export const SceneTechClosing: React.FC = () => {
  const frame = useCurrentFrame();

  return (
    <div style={{
      width: "100%", height: "100%", background: "#0D1512",
      display: "flex", alignItems: "center", justifyContent: "center",
      fontFamily: "'Inter', system-ui, sans-serif", overflow: "hidden", position: "relative"
    }}>
      {/* Floating code lines */}
      <div style={{ position: "absolute", inset: 0, opacity: 0.15 }}>
        {codeLines.map((line, i) => {
          const y = interpolate(frame, [0 + i * 3, 40 + i * 3], [720, -40], { extrapolateRight: "clamp" });
          const x = 80 + (i % 3) * 480;
          const o = interpolate(frame, [0 + i * 3, 20 + i * 3, 40 + i * 3], [0, 1, 0], { extrapolateRight: "clamp" });
          return (
            <div key={i} style={{ position: "absolute", left: x, top: y, opacity: o, fontFamily: "monospace", fontSize: 13, color: "#18B66B", whiteSpace: "nowrap" }}>
              {line}
            </div>
          );
        })}
      </div>

      {/* Server icons orbit */}
      <div style={{ position: "absolute", right: 120, top: "50%", transform: "translateY(-50%)", opacity: 0.2 }}>
        {serverIcons.map((icon, i) => {
          const angle = interpolate(frame, [0, 120], [0 + i * 72, 360 + i * 72]);
          const radius = 140;
          const x = Math.cos((angle * Math.PI) / 180) * radius;
          const y = Math.sin((angle * Math.PI) / 180) * radius;
          return (
            <div key={i} style={{ position: "absolute", left: x, top: y, fontSize: 28, transform: "translate(-50%, -50%)" }}>
              {icon}
            </div>
          );
        })}
      </div>

      {/* Center content */}
      <div style={{ textAlign: "center", zIndex: 1 }}>
        <div style={{
          width: 64, height: 64, borderRadius: 16,
          background: "linear-gradient(135deg, #0F8F54, #18B66B)",
          display: "grid", placeItems: "center", margin: "0 auto 24px",
          fontSize: 28, fontWeight: 800, color: "#fff"
        }}>E</div>
        <h2 style={{ fontSize: 32, fontWeight: 800, color: "#F3F4F8", margin: "0 0 8px", letterSpacing: "-0.02em" }}>Eco Finanças</h2>
        <p style={{ fontSize: 16, color: "#6B7280", margin: "0 0 32px" }}>Java Spring Boot + Next.js</p>
        <div style={{
          display: "inline-flex", alignItems: "center", gap: 10,
          padding: "12px 20px", borderRadius: 10, background: "#1A1D2B",
          border: "1px solid #2D3142", fontSize: 14, color: "#18B66B", fontWeight: 600
        }}>
          <span style={{ fontSize: 18 }}>🔗</span>
          github.com/Pedro3495/eco
        </div>
      </div>

      {/* Bottom dashboard card peek */}
      <div style={{
        position: "absolute", bottom: 60, left: "50%", transform: "translateX(-50%)",
        opacity: interpolate(frame, [60, 100], [0, 1], { extrapolateRight: "clamp" })
      }}>
        <div style={{
          padding: "16px 24px", borderRadius: 14,
          background: "linear-gradient(135deg, #0F8F54, #18B66B)",
          color: "#fff", textAlign: "center", minWidth: 280
        }}>
          <p style={{ fontSize: 12, opacity: 0.75, margin: 0 }}>Saldo do mês</p>
          <p style={{ fontSize: 28, fontWeight: 800, margin: "4px 0 0" }}>R$ 12.450,00</p>
        </div>
      </div>
    </div>
  );
};
