"use client";

import Link from "next/link";

export default function NotFoundView() {
  return (
    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        gap: "16px",
        fontFamily: "'Segoe UI', sans-serif",
        textAlign: "center",
        padding: "24px",
      }}
    >
      <div style={{ fontSize: "64px", fontWeight: "700", color: "#111" }}>404</div>
      <p style={{ margin: 0, fontSize: "16px", color: "#444" }}>
        The page you're looking for doesn't exist.
      </p>
      <Link
        href="/"
        style={{
          marginTop: "8px",
          padding: "10px 20px",
          borderRadius: "8px",
          backgroundColor: "#111111",
          color: "#ffffff",
          fontSize: "14px",
          fontWeight: "600",
          textDecoration: "none",
        }}
      >
        Back to Home
      </Link>
    </div>
  );
}
