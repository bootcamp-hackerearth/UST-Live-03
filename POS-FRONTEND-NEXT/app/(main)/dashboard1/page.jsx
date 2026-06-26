"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";

const styles = {
  page: {
    minHeight: "100vh",
    padding: "30px",
    background: "#f8fafc",
    fontFamily: "Inter, sans-serif",
    boxSizing: "border-box",
  },

  topbar: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "28px",
    gap: "20px",
    flexWrap: "wrap",
  },

  pageTitle: {
    margin: 0,
    fontSize: "30px",
    color: "#111827",
    fontWeight: "700",
  },

  pageSubtitle: {
    marginTop: "6px",
    color: "#6b7280",
    fontSize: "14px",
  },

  profile: {
    display: "flex",
    alignItems: "center",
    gap: "12px",
    background: "#fff",
    padding: "10px 14px",
    borderRadius: "14px",
    boxShadow: "0 4px 18px rgba(0,0,0,0.05)",
    cursor: "pointer",
  },

  avatar: {
    width: "42px",
    height: "42px",
    borderRadius: "50%",
    background: "linear-gradient(135deg, #6366f1, #8b5cf6)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    color: "#fff",
    fontWeight: "700",
    fontSize: "16px",
    flexShrink: 0,
  },

  profileName: {
    margin: 0,
    fontSize: "14px",
    color: "#111827",
  },

  profileRole: {
    margin: 0,
    fontSize: "12px",
    color: "#6b7280",
  },

  heroCard: {
    background: "linear-gradient(135deg, #6366f1, #8b5cf6)",
    borderRadius: "24px",
    padding: "35px",
    color: "#fff",
    boxShadow: "0 12px 30px rgba(99,102,241,0.25)",
  },

  heroTitle: {
    margin: 0,
    fontSize: "30px",
    fontWeight: "700",
  },

  heroText: {
    marginTop: "12px",
    color: "rgba(255,255,255,0.9)",
    lineHeight: "1.6",
    maxWidth: "500px",
  },
};

export default function Dashboard1() {
  const router = useRouter();

  const [user, setUser] = useState({
    name: "Admin",
    role: "Administrator",
  });

  const fetchUser = useCallback(async () => {
    try {
      const token = localStorage.getItem("token");
      const username = localStorage.getItem("username");

      if (!token || !username) {
        router.push("/login");
        return;
      }

      const res = await axios.get("http://localhost:8080/api/user/get", {
        params: { username },
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setUser({
        name: res.data?.name || username,
        role: Array.isArray(res.data?.role)
          ? res.data.role.join(", ")
          : res.data?.role || "Administrator",
      });
    } catch (err) {
      console.error("USER FETCH ERROR:", err);

      setUser({
        name: localStorage.getItem("username") || "Admin",
        role: "Administrator",
      });
    }
  }, [router]);

  useEffect(() => {
    fetchUser();
  }, [fetchUser]);

  return (
    <div style={styles.page}>
      <div style={styles.topbar}>
        <div>
          <h1 style={styles.pageTitle}>Dashboard</h1>
          <p style={styles.pageSubtitle}>Welcome back</p>
        </div>
        <button
          type="button"
          onClick={() => router.push("/cart")}
          style={{
            padding: "12px 18px",
            border: "none",
            borderRadius: "12px",
            cursor: "pointer",
            background: "#111827",
            color: "#fff",
            fontWeight: "600",
          }}
        >
          🛒 Cart
        </button>

        {/* FIXED PROFILE BUTTON */}
        <button
          type="button"
          aria-label="Open user profile"
          onClick={() => router.push("/user/profile")}
          style={{
            ...styles.profile,
            border: "none",
          }}
        >
          <div style={styles.avatar}>
            {user.name?.charAt(0)?.toUpperCase() || "A"}
          </div>

          <div>
            <h4 style={styles.profileName}>{user.name}</h4>
            <p style={styles.profileRole}>{user.role}</p>
          </div>
        </button>
      </div>

      <div style={styles.heroCard}>
        <h2 style={styles.heroTitle}>Manage your POS business</h2>

        <p style={styles.heroText}>
          Track sales, orders and users in one place.
        </p>
      </div>
    </div>
  );
}
