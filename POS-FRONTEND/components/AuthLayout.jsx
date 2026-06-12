"use client";

import PropTypes from "prop-types";

export const C = {
  navy:    "#363955",
  mid:     "#54668E",
  light:   "#879EC6",
  gray:    "#E8E8E8",
  offWhite:"#F5F6E6",
  text:    "#1e2235",
  muted:   "#6b7280",
  white:   "#ffffff",
  error:   "#c0392b",
  errorBg: "#fdf2f2",
  errorBdr:"#f5c6c6",
  successBg:   "#f0fdf4",
  successBdr:  "#86efac",
  successText: "#166534",
};

export const sharedStyles = {
  container: {
    minHeight: "100vh",
    backgroundColor: "#f0f1f6",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontFamily: "'Segoe UI', sans-serif",
    padding: "20px",
    boxSizing: "border-box",
  },
  cardWrapper: (maxWidth = "400px") => ({
    width: "100%",
    maxWidth,
  }),
  card: {
    background: C.white,
    borderRadius: "14px",
    border: "1.5px solid #e8eaf0",
    boxShadow: "0 1px 24px rgba(54,57,85,0.09)",
    overflow: "hidden",
  },
  header: {
    padding: "24px 32px 20px",
    borderBottom: "1.5px solid #e8eaf0",
    textAlign: "center",
  },
  logoIcon: {
    display: "inline-flex",
    alignItems: "center",
    justifyContent: "center",
    width: "44px",
    height: "44px",
    borderRadius: "11px",
    background: `linear-gradient(135deg, ${C.navy}, ${C.mid})`,
    marginBottom: "12px",
    boxShadow: "0 4px 14px rgba(54,57,85,0.25)",
  },
  submitBtn: (loading) => ({
    width: "100%",
    padding: "11px",
    background: loading ? "#c4c8d4" : `linear-gradient(135deg, ${C.navy}, ${C.mid})`,
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    fontSize: "13px",
    fontWeight: "600",
    cursor: loading ? "not-allowed" : "pointer",
    letterSpacing: "0.3px",
    boxShadow: loading ? "none" : "0 3px 10px rgba(54,57,85,0.25)",
  }),
  label: {
    display: "block",
    fontSize: "11px",
    fontWeight: "700",
    color: "#4b5563",
    marginBottom: "6px",
    letterSpacing: "0.4px",
    textTransform: "uppercase",
  },
  input: (hasError) => ({
    width: "100%",
    padding: "10px 13px",
    border: `1.5px solid ${hasError ? C.error : C.gray}`,
    borderRadius: "8px",
    fontSize: "13px",
    outline: "none",
    boxSizing: "border-box",
    backgroundColor: hasError ? C.errorBg : "#fafafa",
    color: C.text,
  }),
  footerText: {
    textAlign: "center",
    marginTop: "20px",
    fontSize: "13px",
    color: C.muted,
  },
};

export default function AuthLayout({ children, maxWidth = "400px" }) {
  return (
    <div style={sharedStyles.container}>
      <div style={sharedStyles.cardWrapper(maxWidth)}>
        <div style={sharedStyles.card}>
          <div style={sharedStyles.header}>
            <div style={sharedStyles.logoIcon}>
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" aria-hidden="true">
                <path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z" stroke="#fff" strokeWidth="1.8" strokeLinejoin="round"/>
                <path d="M3 6h18M16 10a4 4 0 01-8 0" stroke="#fff" strokeWidth="1.8" strokeLinecap="round"/>
              </svg>
            </div>
            <h1 style={{ margin: "0 0 3px", fontSize: "20px", fontWeight: "700", color: C.navy, letterSpacing: "-0.3px" }}>
              StoreFlow
            </h1>
            <p style={{ margin: 0, fontSize: "12px", color: C.muted }}>Retail Management</p>
          </div>
          {children}
        </div>
        <p style={{ textAlign: "center", marginTop: "16px", fontSize: "12px", color: "#9ca3af" }}>
          RetailPOS © 2026
        </p>
      </div>
    </div>
  );
}

AuthLayout.propTypes = {
  children: PropTypes.node.isRequired,
  maxWidth: PropTypes.string,
};