"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import api from "@/api/axios";
import { authSharedStyles } from "@/components/authStyles"; 

const styles = {
  ...authSharedStyles,
  container: {
    display: "flex",
    width: "100%",
    maxWidth: "900px",
    height: "500px",
    borderRadius: "8px",
    overflow: "hidden",
    boxShadow: "0 12px 48px rgba(0,0,0,0.12)",
  },
  header: { marginBottom: "24px" },
  field: { marginBottom: "18px" },
  subtitle: { ...authSharedStyles.subtitle, marginBottom: "24px" },
};

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [serverError, setServerError] = useState("");
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  function validate() {
    const errors = {};
    if (!username.trim()) errors.username = "Username is required.";
    if (!password.trim()) errors.password = "Password is required.";
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  }

  async function handleLogin(e) {
    e.preventDefault();
    if (!validate()) return;
    setLoading(true);
    setServerError("");
    try {
      const response = await api.post("/authenticate", { username, password });
      if (response.data?.token) {
        localStorage.setItem("token", response.data.token);
        router.push("/home");
      } else {
        setServerError("Invalid username or password.");
      }
    } catch (err) {
      if (err.response?.status === 401) {
        setServerError("Invalid username or password.");
      } else {
        setServerError("Cannot reach server. Please check your connection.");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <div style={styles.leftPanel}>
          <div style={styles.panelIcon}>💼</div>
          <h2 style={styles.panelTitle}>RetailPOS</h2>
          <p style={styles.panelSubtitle}>
            Your complete retail management solution. Fast checkouts. Smart inventory. Complete control.
          </p>
        </div>
        <div style={styles.rightPanel}>
          <div style={styles.header}>
            <h1 style={styles.title}>Sign In</h1>
            <p style={styles.subtitle}>Access your RetailPOS account</p>
          </div>
          {serverError && <div style={styles.errorBox}>{serverError}</div>}
          <form onSubmit={handleLogin} noValidate>
            <div style={styles.field}>
              <label style={styles.label} htmlFor="username">Username</label>
              <input
                id="username" type="text"
                style={{ ...styles.input, ...(fieldErrors.username ? styles.inputError : {}) }}
                placeholder="Enter your username"
                value={username}
                onChange={(e) => {
                  setUsername(e.target.value);
                  if (fieldErrors.username) setFieldErrors(p => ({ ...p, username: "" }));
                }}
              />
              {fieldErrors.username && <p style={styles.errorText}>{fieldErrors.username}</p>}
            </div>
            <div style={styles.field}>
              <label style={styles.label} htmlFor="password">Password</label>
              <input
                id="password" type="password"
                style={{ ...styles.input, ...(fieldErrors.password ? styles.inputError : {}) }}
                placeholder="Enter your password"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value);
                  if (fieldErrors.password) setFieldErrors(p => ({ ...p, password: "" }));
                }}
              />
              {fieldErrors.password && <p style={styles.errorText}>{fieldErrors.password}</p>}
            </div>
            <button
              type="submit"
              className="sign-in-button"
              style={{ ...styles.button, ...(loading ? styles.buttonDisabled : {}) }}
              disabled={loading}
            >
              {loading ? "Signing in…" : "Sign In"}
            </button>
          </form>
          <div style={styles.footer}>
            Don't have an account?
            <Link href="/register" style={styles.link}>Create one</Link>
          </div>
        </div>
      </div>
    </div>
  );
}