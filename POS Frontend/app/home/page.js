"use client";

import { useState, useEffect } from "react";

export default function Home() {
  const [sidebarOpen, setSidebarOpen] = useState(true);

  useEffect(() => {
    const handleToggle = (e) => {
      setSidebarOpen(e.detail.isOpen);
    };

    globalThis.addEventListener("sidebar-toggle", handleToggle);
    return () => globalThis.removeEventListener("sidebar-toggle", handleToggle);
  }, []);

  return (
    <div
      style={{
        ...styles.page,
        paddingLeft: sidebarOpen ? "220px" : "55px"
      }}
    >
      <div style={styles.card}>

        <div style={styles.topSection}>
          <div style={styles.badge}>Point of Sale System</div>

          <h1 style={styles.title}>
            Welcome to <span style={styles.brand}>RetailPOS</span>
          </h1>

          <p style={styles.subtitle}>
            Fast. Simple. Smart retail management — built for modern businesses.
          </p>
        </div>

        <div style={styles.bottomStrip}>
          <span style={styles.bottomText}>
            RetailPOS · Built for modern retail teams · v2.0
          </span>
        </div>

      </div>
    </div>
  );
}

const styles = {
  page: {
    width: "100%",
    minHeight: "calc(100vh - 60px)",
    backgroundColor: "#ffffff",
    fontFamily: "'Segoe UI', sans-serif",
    display: "flex",
    flexDirection: "column",
    transition: "padding-left 0.2s ease",
    boxSizing: "border-box",
    marginTop: "60px",
  },
  card: {
    flex: 1,
    backgroundColor: "#f4f4f4",
    display: "flex",
    flexDirection: "column",
    justifyContent: "space-between",
    overflow: "hidden",
    borderLeft: "1px solid #d1d5db",
  },
  topSection: {
    flex: 1,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    padding: "48px 40px 24px",
    textAlign: "center",
    background: "#ffffff",
  },
  badge: {
    display: "inline-block",
    padding: "6px 16px",
    borderRadius: "999px",
    backgroundColor: "#f4f4f4",
    color: "#1a1a1a",
    fontSize: "11px",
    fontWeight: "700",
    letterSpacing: "1.2px",
    textTransform: "uppercase",
    marginBottom: "16px",
    border: "1px solid #d1d5db",
  },
  title: {
    fontSize: "36px",
    fontWeight: "800",
    color: "#1a1a1a",
    marginBottom: "12px",
    letterSpacing: "-0.5px",
  },
  brand: {
    color: "#1a1a1a",
    borderBottom: "3px solid #1a1a1a",
    paddingBottom: "2px",
  },
  subtitle: {
    fontSize: "15px",
    color: "#666666",
    maxWidth: "480px",
    lineHeight: "1.7",
  },
  features: {
    display: "flex",
    justifyContent: "center",
    gap: "24px",
    padding: "0 40px 40px",
    flexWrap: "wrap",
  },
  featureBox: {
    backgroundColor: "#ffffff",
    border: "1px solid #d1d5db",
    padding: "28px 24px",
    borderRadius: "6px",
    width: "220px",
    textAlign: "center",
    flexShrink: 0,
    transition: "all 0.3s ease",
    boxShadow: "0 2px 8px rgba(0,0,0,0.04)",
  },
  featureBoxAccent: {
    background: "linear-gradient(135deg, #000000, #3f3f3f)",
    border: "1px solid #000000",
    boxShadow: "0 12px 32px rgba(0,0,0,0.18)",
    transform: "translateY(-4px)",
  },
  featureTitle: {
    fontSize: "14px",
    fontWeight: "800",
    color: "#1a1a1a",
    marginBottom: "8px",
  },
  featureText: {
    fontSize: "13px",
    color: "#666666",
    lineHeight: "1.6",
  },
  bottomStrip: {
    background: "#1a1a1a",
    padding: "14px 40px",
    textAlign: "center",
    flexShrink: 0,
  },
  bottomText: {
    color: "rgba(255,255,255,0.9)",
    fontSize: "12px",
    fontWeight: "500",
    letterSpacing: "0.3px",
  },
};