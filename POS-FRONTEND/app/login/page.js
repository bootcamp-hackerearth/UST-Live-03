"use client";

import { useState } from "react";
import api from "@/services/api";
import { useRouter } from "next/navigation";
import Link from "next/link";

const Login = () => {
  const [credentials, setCredentials] = useState({
    username: "",
    password: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const handleChange = (e) => {
    setCredentials({
      ...credentials,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    if (!credentials.username || !credentials.password) {
      setError("Please fill all fields");
      setLoading(false);
      return;
    }
    
    try {
      const response = await api.post("/api/authenticate", credentials);
      const data = response.data;

      if (data.token && data.token !== "Error") {
        localStorage.setItem("token", data.token);
        localStorage.setItem("username", credentials.username);

        router.push("/dashboard1");
      } else {
        setError("Incorrect username or password ❌");
      }
    } catch {
  setError("Server error ❗");
}

    setLoading(false);
  };

  return (
    <div style={styles.container}>
      <form onSubmit={handleSubmit} style={styles.card}>
        <h2 style={styles.title}>Login</h2>

        <input
          name="username"
          placeholder="Username"
          value={credentials.username}
          onChange={handleChange}
          style={styles.input}
        />

        <input
          name="password"
          type="password"
          placeholder="Password"
          value={credentials.password}
          onChange={handleChange}
          style={styles.input}
        />

        {error && <p style={styles.error}>{error}</p>}

        <button type="submit" disabled={loading} style={styles.button}>
          {loading ? "Logging in..." : "Login"}
        </button>

        <p style={styles.text}>
          Don’t have account?{" "}
          <Link href="/register" style={styles.link}>
            Register
          </Link>
        </p>
      </form>
    </div>
  );
};

export default Login;

/* ================= STYLES ================= */
const styles = {
  container: {
    minHeight: "100vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    background: "#f8fafc",
    fontFamily: "'Inter', sans-serif",
    padding: "20px",
    position: "relative",
    overflow: "hidden",
  },

  // Background circles
  bgCircle1: {
    position: "absolute",
    width: "350px",
    height: "350px",
    borderRadius: "50%",
    background: "rgba(99,102,241,0.15)",
    top: "-120px",
    left: "-100px",
    filter: "blur(30px)",
  },

  bgCircle2: {
    position: "absolute",
    width: "300px",
    height: "300px",
    borderRadius: "50%",
    background: "rgba(139,92,246,0.15)",
    bottom: "-100px",
    right: "-80px",
    filter: "blur(30px)",
  },

  card: {
    width: "100%",
    maxWidth: "420px",
    background: "rgba(255,255,255,0.9)",
    backdropFilter: "blur(10px)",
    padding: "38px",
    borderRadius: "24px",
    display: "flex",
    flexDirection: "column",
    gap: "18px",
    boxShadow: "0 10px 40px rgba(0,0,0,0.08)",
    border: "1px solid rgba(255,255,255,0.4)",
    position: "relative",
    zIndex: 10,
  },

  logoSection: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    marginBottom: "10px",
  },

  logoCircle: {
    width: "72px",
    height: "72px",
    borderRadius: "18px",
    background: "linear-gradient(135deg, #6366f1, #8b5cf6)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    color: "#fff",
    fontWeight: "700",
    fontSize: "30px",
    marginBottom: "15px",
    boxShadow: "0 10px 25px rgba(99,102,241,0.3)",
  },

  title: {
    margin: 0,
    fontSize: "30px",
    fontWeight: "700",
    color: "#111827",
  },

  subtitle: {
    marginTop: "8px",
    fontSize: "14px",
    color: "#6b7280",
    textAlign: "center",
  },

  inputGroup: {
    display: "flex",
    flexDirection: "column",
    gap: "6px",
  },

  label: {
    fontSize: "13px",
    fontWeight: "600",
    color: "#374151",
  },

  input: {
    padding: "14px",
    borderRadius: "14px",
    border: "1px solid #e5e7eb",
    fontSize: "14px",
    outline: "none",
    transition: "0.3s",
    background: "#fff",
    color: "#111827",
    boxSizing: "border-box",
  },

  button: {
    padding: "14px",
    border: "none",
    borderRadius: "14px",
    background: "linear-gradient(135deg, #6366f1, #8b5cf6)",
    color: "#fff",
    fontSize: "15px",
    fontWeight: "700",
    cursor: "pointer",
    marginTop: "8px",
    transition: "0.3s ease",
    boxShadow: "0 10px 20px rgba(99,102,241,0.25)",
  },

  error: {
    background: "#fee2e2",
    color: "#dc2626",
    padding: "12px",
    borderRadius: "12px",
    textAlign: "center",
    fontSize: "14px",
    border: "1px solid #fecaca",
  },

  text: {
    textAlign: "center",
    marginTop: "8px",
    color: "#6b7280",
    fontSize: "14px",
  },

  link: {
    color: "#6366f1",
    textDecoration: "none",
    fontWeight: "600",
  },
};