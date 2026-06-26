"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import api from "@/services/api";

export default function UserProfile() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);

  const [user, setUser] = useState({
    name: "",
    phoneNo: "",
    username: "",
    roles: [],
  });

  const fetchUser = useCallback(async () => {
    try {
      const token = localStorage.getItem("token");
      const username =
        localStorage.getItem("username");

      if (!token || !username) {
        router.push("/login");
        return;
      }

      const res = await api.get(
        `/api/user/get?username=${username}`
      );

      setUser({
        name: res.data?.name || "-",
        phoneNo: res.data?.phoneNo || "-",
        username:
          res.data?.username || username,
        roles: res.data?.roles || [],
      });
    } catch (err) {
      console.error("PROFILE FETCH ERROR:", err);
    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => {
    fetchUser();
  }, [fetchUser]);

  if (loading) {
    return (
      <div style={styles.loading}>
        Loading profile...
      </div>
    );
  }

  return (
    <div style={styles.wrapper}>
      <div style={styles.card}>
        {/* Header */}
        <div style={styles.header}>
          <div style={styles.avatar}>
            {user.name?.charAt(0)?.toUpperCase() ||
              "A"}
          </div>

          <div>
            <h2 style={styles.title}>
              {user.name}
            </h2>

            <p style={styles.subtitle}>
              User Profile
            </p>
          </div>
        </div>

        {/* Details */}
        <div style={styles.section}>
          <div style={styles.row}>
            <span style={styles.label}>
              Name
            </span>

            <span style={styles.value}>
              {user.name}
            </span>
          </div>

          <div style={styles.row}>
            <span style={styles.label}>
              Phone No
            </span>

            <span style={styles.value}>
              {user.phoneNo}
            </span>
          </div>

          <div style={styles.row}>
            <span style={styles.label}>
              Username
            </span>

            <span style={styles.value}>
              {user.username}
            </span>
          </div>

          <div style={styles.row}>
            <span style={styles.label}>
              Roles
            </span>

            <span style={styles.value}>
              {user.roles.length > 0
                ? user.roles.join(", ")
                : "-"}
            </span>
          </div>
        </div>

        <button
          style={styles.button}
          onClick={() =>
            router.push("/dashboard1")
          }
        >
          ← Back to Dashboard
        </button>
      </div>
    </div>
  );
}

const styles = {
  wrapper: {
    height: "100vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    background: "#f5f7fb",
    fontFamily: "Arial, sans-serif",
  },

  card: {
    width: "420px",
    background: "#ffffff",
    borderRadius: "18px",
    padding: "25px",
    boxShadow:
      "0 10px 25px rgba(0,0,0,0.1)",
  },

  header: {
    display: "flex",
    alignItems: "center",
    gap: "15px",
    marginBottom: "20px",
  },

  avatar: {
    width: "55px",
    height: "55px",
    borderRadius: "50%",
    background: "#006E74",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    color: "#fff",
    fontWeight: "bold",
    fontSize: "20px",
  },

  title: {
    margin: 0,
    fontSize: "20px",
    color: "#111827",
  },

  subtitle: {
    margin: "2px 0 0",
    fontSize: "13px",
    color: "#6b7280",
  },

  section: {
    marginTop: "10px",
  },

  row: {
    display: "flex",
    justifyContent: "space-between",
    padding: "12px 0",
    borderBottom: "1px solid #eee",
    fontSize: "14px",
  },

  label: {
    color: "#6b7280",
  },

  value: {
    color: "#111827",
    fontWeight: "600",
  },

  button: {
    marginTop: "20px",
    width: "100%",
    padding: "12px",
    border: "none",
    borderRadius: "10px",
    background: "#231F20",
    color: "#fff",
    fontWeight: "600",
    cursor: "pointer",
  },

  loading: {
    height: "100vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    fontSize: "18px",
    color: "#6b7280",
  },
};