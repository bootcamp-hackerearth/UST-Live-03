"use client";

import React from "react";
import { useRouter, useSearchParams } from "next/navigation";

export default function ErrorPage() {
  const router = useRouter();
  const params = useSearchParams();

  const status = params.get("status") || "403";

  const config = {
    401: {
      code: "401",
      title: "Unauthorized",
      message: "You must log in to access this page.",
      colorBg: "bg-blue-100",
      colorText: "text-blue-600",
    },
    403: {
      code: "403",
      title: "Access Denied",
      message: "You do not have permission to access this page.",
      colorBg: "bg-amber-100",
      colorText: "text-amber-600",
    },
    500: {
      code: "500",
      title: "Server Error",
      message: "Something went wrong on the server. Please try again later.",
      colorBg: "bg-red-100",
      colorText: "text-red-600",
    },
  };

  const current = config[status] || config["403"];

  return (
    <div
      style={{
        minHeight: "100vh",
        background: "#f5f5f5",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        padding: 20,
      }}
    >
      <div
        style={{
          width: "100%",
          maxWidth: 420,
          background: "#fff",
          borderRadius: 16,
          border: "1px solid #e5e7eb",
          boxShadow: "0 4px 20px rgba(0,0,0,0.08)",
          padding: 40,
          textAlign: "center",
        }}
      >
        <div
          className={`${current.colorBg} ${current.colorText}`}
          style={{
            width: 64,
            height: 64,
            margin: "0 auto 24px",
            borderRadius: "50%",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontSize: 24,
            fontWeight: 700,
          }}
        >
          {current.code}
        </div>

        <h1
          style={{
            fontSize: 22,
            fontWeight: 600,
            color: "#111",
            marginBottom: 8,
          }}
        >
          {current.title}
        </h1>

        <p style={{ color: "#6b7280", fontSize: 14, marginBottom: 32 }}>
          {current.message}
        </p>

        <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
          <button
            onClick={() => router.push("/dashboard")}
            style={{
              width: "100%",
              background: "#111",
              color: "#fff",
              border: "none",
              padding: "12px 0",
              borderRadius: 10,
              fontSize: 14,
              fontWeight: 500,
              cursor: "pointer",
              transition: "0.2s",
            }}
            onMouseOver={(e) => (e.currentTarget.style.background = "#000")}
            onMouseOut={(e) => (e.currentTarget.style.background = "#111")}
            onFocus={(e) => (e.currentTarget.style.background = "#000")}
            onBlur={(e) => (e.currentTarget.style.background = "#111")}
          >
            Return to Dashboard
          </button>

          <button
            onClick={() => router.back()}
            style={{
              width: "100%",
              background: "#fff",
              color: "#111",
              border: "1px solid #d1d5db",
              padding: "12px 0",
              borderRadius: 10,
              fontSize: 14,
              fontWeight: 500,
              cursor: "pointer",
              transition: "0.2s",
            }}
            onMouseOver={(e) => (e.currentTarget.style.background = "#f5f5f5")}
            onMouseOut={(e) => (e.currentTarget.style.background = "#fff")}
            onFocus={(e) => (e.currentTarget.style.background = "#f5f5f5")}
            onBlur={(e) => (e.currentTarget.style.background = "#fff")}
          >
            Go Back
          </button>
        </div>
      </div>
    </div>
  );
}