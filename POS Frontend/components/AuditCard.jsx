"use client";

import PropTypes from "prop-types";

export const metadataOuterSt = {
    marginTop: "28px",
    padding: "20px",
    background: "#ffffff",
    borderRadius: "8px",
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: "32px",
    boxSizing: "border-box",
};

const metadataCardSt = {
    display: "flex",
    flexDirection: "column",
    gap: "12px",
};

const metadataCardTitleSt = {
    margin: 0,
    fontSize: "12px",
    fontWeight: "600",
    color: "#6b7280",
    letterSpacing: "0.5px",
    textTransform: "uppercase",
    marginBottom: "4px",
};

const metadataRowSt = {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    fontSize: "13px",
    color: "#1f2937",
    lineHeight: "1.6",
    gap: "16px",
};

const metadataLabelSt = {
    color: "#6b7280",
    fontWeight: "500",
    fontSize: "13px",
};

const metadataValueSt = {
    fontWeight: "500",
    color: "#1a1a1a",
    textAlign: "right",
    fontSize: "13px",
};

export function formatDateTime(value) {
    if (!value) return "—";
    const d = new Date(value);
    if (Number.isNaN(d.getTime())) return value;
    return d.toLocaleString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });
}

export default function AuditCard({ heading, rows }) {
    return (
        <div style={metadataCardSt}>
            <p style={metadataCardTitleSt}>{heading}</p>
            {rows.map(({ label, value }) => (
                <div key={label} style={metadataRowSt}>
                    <span style={metadataLabelSt}>{label}</span>
                    <span style={metadataValueSt}>{value || "—"}</span>
                </div>
            ))}
        </div>
    );
}

AuditCard.propTypes = {
    heading: PropTypes.string.isRequired,
    rows: PropTypes.arrayOf(
        PropTypes.shape({
            label: PropTypes.string.isRequired,
            value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        })
    ).isRequired,
};
