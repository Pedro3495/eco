import type { Metadata, Viewport } from "next";
import { Inter } from "next/font/google";
import "./globals.css";

const inter = Inter({
  subsets: ["latin"],
  display: "swap",
  variable: "--font-inter"
});

export const metadata: Metadata = {
  title: "Eco Finanças",
  description: "PWA pessoal de controle financeiro. Acompanhe receitas, despesas, orçamentos e metas de forma simples e elegante.",
  applicationName: "Eco Finanças",
  appleWebApp: {
    capable: true,
    title: "Eco",
    statusBarStyle: "black-translucent"
  },
  icons: {
    icon: "/icon.svg",
    apple: "/icon.svg"
  },
  manifest: "/manifest.webmanifest",
  formatDetection: {
    telephone: false
  }
};

export const viewport: Viewport = {
  themeColor: "#5B42C5",
  width: "device-width",
  initialScale: 1,
  maximumScale: 5,
  userScalable: true
};

export default function RootLayout({
  children
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pt-BR" suppressHydrationWarning>
      <body className={inter.variable}>
        <a href="#main-content" className="skip-link">
          Pular para conteúdo principal
        </a>
        <div id="main-content">{children}</div>
      </body>
    </html>
  );
}
