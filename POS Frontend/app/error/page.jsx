"use client";

import { useEffect } from "react";
import Link from "next/link";
import { statusPageStyles as styles } from "@/components/statusPageStyles";

export default function ServerError() {
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      globalThis.location.href = "/login";
    }
  }, []);

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <div style={styles.icon}>⚠️</div>
        <p style={styles.code}>Error 500</p>
        <h1 style={styles.title}>Something Went Wrong</h1>
        <p style={styles.message}>
          An unexpected server error occurred. Please try again in a few moments.
        </p>
        <div style={styles.actions}>
          <button type="button" style={styles.buttonSecondary} onClick={() => globalThis.location.reload()}>
            Try Again
          </button>
          <Link href="/home" style={styles.button}>Go to Home</Link>
        </div>
      </div>
    </div>
  );
}
