"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import api from "../../services/api";

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
      const response = await api.post(
        "/api/authenticate",
        credentials
      );

      const data = response.data;

      if (data.token && data.token !== "Error") {
        localStorage.setItem("token", data.token);
        localStorage.setItem("username", credentials.username);

        router.push("/dashboard1");
      } else {
        setError("Incorrect username or password ❌");
      }
    } catch (error) {
      console.error("Login error:", error);
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
          type="text"
          placeholder="Enter Username"
          value={credentials.username}
          onChange={handleChange}
          style={styles.input}
        />

        <input
          name="password"
          type="password"
          placeholder="Enter Password"
          value={credentials.password}
          onChange={handleChange}
          style={styles.input}
        />

        {error && <p style={styles.error}>{error}</p>}

        <button
          type="submit"
          disabled={loading}
          style={styles.button}
        >
          {loading ? "Logging in..." : "Login"}
        </button>

        <p style={styles.text}>
          Don&apos;t have account?{" "}
          <Link href="/register" style={styles.link}>
            Register
          </Link>
        </p>
      </form>
    </div>
  );
};

export default Login;

const styles = {
  container: {
    minHeight: "100vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    background: "#f3f4f6",
    padding: "20px",
    fontFamily: "Inter, sans-serif",
  },

  card: {
    width: "100%",
    maxWidth: "390px", // smaller card
    background: "#ffffff",
    padding: "32px",
    borderRadius: "24px",
    display: "flex",
    flexDirection: "column",
    gap: "16px",
    boxShadow: "0 8px 30px rgba(0,0,0,0.06)",
  },

  title: {
    fontSize: "34px",
    fontWeight: "800",
    color: "#0f172a",
    textAlign: "center",
    margin: 0,
  },

  input: {
    width: "100%",
    padding: "14px",
    borderRadius: "16px",
    border: "1.5px solid #d1d5db",
    fontSize: "15px",
    outline: "none",
    color: "#111827",
    background: "#fff",
    boxSizing: "border-box",
    fontWeight: "500",
  },

  button: {
    width: "100%",
    padding: "14px",
    border: "none",
    borderRadius: "16px",
    background: "linear-gradient(135deg, #6366f1, #8b5cf6)",
    color: "#fff",
    fontSize: "18px",
    fontWeight: "700",
    cursor: "pointer",
    marginTop: "4px",
    boxShadow: "0 8px 18px rgba(99,102,241,0.22)",
  },

  error: {
    background: "#fee2e2",
    color: "#dc2626",
    padding: "10px",
    borderRadius: "10px",
    textAlign: "center",
    fontSize: "13px",
  },

  text: {
    textAlign: "center",
    color: "#6b7280",
    fontSize: "14px",
    marginTop: "2px",
  },

  link: {
    color: "#6366f1",
    textDecoration: "none",
    fontWeight: "700",
  },
};