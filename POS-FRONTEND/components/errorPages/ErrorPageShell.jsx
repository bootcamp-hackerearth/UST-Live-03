"use client";

import PropTypes from "prop-types";

export const C = {
    navy: "#363955",
    mid: "#54668E",
    light: "#879EC6",
    text: "#1e2235",
    muted: "#6b7280",
    bg: "#f4f5f9",
    border: "#e4e6ef",
};

export function ErrorPageLayout({ errorCode, title, message, icon, iconDanger = true, actions }) {
    return (
        <div style={{
            minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center",
            background: C.bg, fontFamily: "'Segoe UI', sans-serif", padding: "20px",
        }}>
            <div style={{
                background: "#ffffff", borderRadius: "14px", border: `1.5px solid ${C.border}`,
                boxShadow: "0 4px 24px rgba(54,57,85,0.08)", padding: "48px 40px",
                maxWidth: "420px", width: "100%", textAlign: "center",
            }}>
                <div style={{
                    width: "72px", height: "72px", borderRadius: "50%",
                    background: iconDanger ? "#fef2f2" : C.bg,
                    border: iconDanger ? "1.5px solid #fecaca" : `1.5px solid ${C.border}`,
                    display: "flex", alignItems: "center", justifyContent: "center",
                    margin: "0 auto 22px",
                }}>
                    {icon}
                </div>
                <div style={{
                    fontSize: "13px", fontWeight: "700", color: C.light,
                    letterSpacing: "0.08em", marginBottom: "6px",
                }}>
                    ERROR {errorCode}
                </div>
                <h1 style={{ margin: "0 0 10px", fontSize: "20px", fontWeight: "700", color: C.navy }}>
                    {title}
                </h1>
                <p style={{ margin: "0 0 30px", fontSize: "13.5px", color: C.muted, lineHeight: 1.65 }}>
                    {message}
                </p>
                <div style={{ display: "flex", gap: "10px", justifyContent: "center" }}>
                    {actions}
                </div>
            </div>
        </div>
    );
}

export function PrimaryButton({ onClick, children }) {
    return (
        <button type="button" onClick={onClick} style={{
            padding: "9px 22px", borderRadius: "8px", border: "none",
            background: `linear-gradient(135deg, ${C.navy}, ${C.mid})`,
            color: "#fff", fontSize: "13px", fontWeight: "700", cursor: "pointer",
            boxShadow: "0 2px 6px rgba(54,57,85,0.20)",
        }}>
            {children}
        </button>
    );
}

export function SecondaryButton({ onClick, children }) {
    return (
        <button type="button" onClick={onClick} style={{
            padding: "9px 22px", borderRadius: "8px",
            border: `1.5px solid ${C.border}`, background: "#ffffff",
            color: C.text, fontSize: "13px", fontWeight: "600", cursor: "pointer",
        }}>
            {children}
        </button>
    );
}

ErrorPageLayout.propTypes = {
    errorCode: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    title: PropTypes.string.isRequired,
    message: PropTypes.node.isRequired,
    icon: PropTypes.node.isRequired,
    iconDanger: PropTypes.bool,
    actions: PropTypes.node.isRequired,
};

PrimaryButton.propTypes = {
    onClick: PropTypes.func.isRequired,
    children: PropTypes.node.isRequired,
};

SecondaryButton.propTypes = {
    onClick: PropTypes.func.isRequired,
    children: PropTypes.node.isRequired,
};