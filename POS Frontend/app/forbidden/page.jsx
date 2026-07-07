"use client";

import { useEffect } from "react";
import Link from "next/link";
import { statusPageStyles as styles } from "@/components/statusPageStyles";

export default function Forbidden() {
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      globalThis.location.href = "/login";
    }
  }, []);

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <div style={styles.icon}>🚫</div>
        <p style={styles.code}>Error 403</p>
        <h1 style={styles.title}>Access Denied</h1>
        <p style={styles.message}>
          You don&apos;t have permission to view this page. If you think this is a mistake, contact your administrator.
        </p>
        <div style={styles.actions}>
          <Link href="/home" style={styles.button}>Go to Home</Link>
        </div>
      </div>
    </div>
  );
}
