"use client";

import React, { Suspense } from "react";
import PropTypes from "prop-types";
import { useRouter, useSearchParams } from "next/navigation";

const CONFIG = {
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

const baseButtonStyle = {
  width: "100%",
  padding: "12px 0",
  borderRadius: 10,
  fontSize: 14,
  fontWeight: 500,
  cursor: "pointer",
  transition: "0.2s",
};

function makeHoverHandlers(defaultBg, hoverBg) {
  return {
    onMouseOver: (e) => (e.currentTarget.style.background = hoverBg),
    onMouseOut: (e) => (e.currentTarget.style.background = defaultBg),
    onFocus: (e) => (e.currentTarget.style.background = hoverBg),
    onBlur: (e) => (e.currentTarget.style.background = defaultBg),
  };
}

function ActionButton({ onClick, style, defaultBg, hoverBg, children }) {
  const hoverHandlers = makeHoverHandlers(defaultBg, hoverBg);

  return (
    <button
      onClick={onClick}
      style={{ ...baseButtonStyle, ...style, background: defaultBg }}
      {...hoverHandlers}
    >
      {children}
    </button>
  );
}

ActionButton.propTypes = {
  onClick: PropTypes.func.isRequired,
  style: PropTypes.object,
  defaultBg: PropTypes.string.isRequired,
  hoverBg: PropTypes.string.isRequired,
  children: PropTypes.node.isRequired,
};

ActionButton.defaultProps = {
  style: {},
};

function ErrorContent() {
  const router = useRouter();
  const params = useSearchParams();

  const status = params.get("status") || "403";
  const current = CONFIG[status] || CONFIG["403"];

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
          <ActionButton
            onClick={() => router.push("/dashboard")}
            style={{ color: "#fff", border: "none" }}
            defaultBg="#111"
            hoverBg="#000"
          >
            Return to Dashboard
          </ActionButton>

          <ActionButton
            onClick={() => router.back()}
            style={{ color: "#111", border: "1px solid #d1d5db" }}
            defaultBg="#fff"
            hoverBg="#f5f5f5"
          >
            Go Back
          </ActionButton>
        </div>
      </div>
    </div>
  );
}

export default function ErrorPage() {
  return (
    <Suspense fallback={null}>
      <ErrorContent />
    </Suspense>
  );
}