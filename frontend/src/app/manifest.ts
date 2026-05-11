import type { MetadataRoute } from "next";

export default function manifest(): MetadataRoute.Manifest {
  return {
    name: "Eco Finanças",
    short_name: "Eco",
    description: "PWA pessoal de controle financeiro.",
    start_url: "/",
    scope: "/",
    display: "standalone",
    background_color: "#F3F4F8",
    theme_color: "#5B42C5",
    orientation: "portrait-primary",
    categories: ["finance", "productivity"],
    lang: "pt-BR",
    icons: [
      {
        src: "/icon.svg",
        sizes: "any",
        type: "image/svg+xml",
        purpose: "any"
      },
      {
        src: "/icon.svg",
        sizes: "any",
        type: "image/svg+xml",
        purpose: "maskable"
      }
    ]
  };
}
