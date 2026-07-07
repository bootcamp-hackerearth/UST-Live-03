"use client";

import Link from "next/link";
import PropTypes from "prop-types";

import { PATHS } from "@/config/constants";

export default function StatusPage({
  statusCode,
  title,
  message,
  buttonLabel = "Back to Dashboard",
}) {
  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <div style={styles.badge}>{statusCode}</div>
        <h1 style={styles.title}>{title}</h1>
        <p style={styles.message}>{message}</p>
        <Link href={PATHS.HOME} style={styles.button}>
          {buttonLabel}
        </Link>
      </div>
    </div>
  );
}

const styles = {
  page: {
    minHeight: "100vh",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    padding: "24px",
    background: "linear-gradient(135deg, #f4f7fb 0%, #eef4fb 100%)",
    fontFamily: "Barlow, sans-serif",
  },
  card: {
    width: "min(100%, 560px)",
    background: "#ffffff",
    border: "1px solid #dce7f2",
    borderRadius: "18px",
    boxShadow: "0 18px 45px rgba(0, 93, 171, 0.12)",
    padding: "40px 32px",
    textAlign: "center",
  },
  badge: {
    display: "inline-flex",
    alignItems: "center",
    justifyContent: "center",
    minWidth: "88px",
    padding: "10px 16px",
    borderRadius: "999px",
    background: "#005dab",
    color: "#ffffff",
    fontSize: "20px",
    fontWeight: 800,
    letterSpacing: "0.04em",
    marginBottom: "18px",
  },
  title: {
    margin: "0 0 12px",
    color: "#0f172a",
    fontSize: "28px",
    fontWeight: 800,
  },
  message: {
    margin: "0 0 24px",
    color: "#4b5563",
    fontSize: "16px",
    lineHeight: 1.7,
  },
  button: {
    display: "inline-flex",
    alignItems: "center",
    justifyContent: "center",
    padding: "12px 18px",
    borderRadius: "10px",
    background: "#e31837",
    color: "#ffffff",
    fontWeight: 700,
    textDecoration: "none",
    transition: "transform 0.15s ease, box-shadow 0.15s ease",
    boxShadow: "0 8px 18px rgba(227, 24, 55, 0.18)",
  },
};

StatusPage.propTypes = {
  statusCode: PropTypes.string.isRequired,
  title: PropTypes.string.isRequired,
  message: PropTypes.string.isRequired,
  buttonLabel: PropTypes.string,
};
