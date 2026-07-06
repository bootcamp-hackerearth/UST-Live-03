"use client";

import Link from "next/link";
import { statusPageStyles as styles } from "@/components/statusPageStyles";

export default function NotFound() {
  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <div style={styles.icon}>🔍</div>
        <p style={styles.code}>Error 404</p>
        <h1 style={styles.title}>Page Not Found</h1>
        <p style={styles.message}>
          The page or resource you&apos;re looking for doesn&apos;t exist or may have been moved.
        </p>
        <div style={styles.actions}>
          <Link href="/home" style={styles.button}>Go to Home</Link>
        </div>
      </div>
    </div>
  );
}
