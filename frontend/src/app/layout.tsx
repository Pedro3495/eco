import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Eco Financas",
  description: "PWA pessoal de controle financeiro"
};

export default function RootLayout({
  children
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pt-BR">
      <body>{children}</body>
    </html>
  );
}

