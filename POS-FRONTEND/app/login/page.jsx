// app/login/page.jsx
"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import api from "@/api/axios";
import AuthLayout, { C, sharedStyles } from "@/components/AuthLayout";

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
        localStorage.setItem("id", response.data.id);
        router.push("/home");
      } else {
        setServerError("Invalid username or password.");
      }
    } catch (err) {
      setServerError(err.response?.status === 401 ? "Invalid username or password." : "Cannot reach server. Please check your connection.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <AuthLayout maxWidth="400px">
      <div style={{ padding: "28px 32px 28px" }}>
        <div style={{ marginBottom: "22px" }}>
          <h2 style={{ margin: "0 0 3px", fontSize: "17px", fontWeight: "700", color: C.navy }}>
            Welcome back
          </h2>
          <p style={{ margin: 0, fontSize: "13px", color: C.muted }}>
            Sign in to your account to continue
          </p>
        </div>

        {serverError && (
          <div style={{ backgroundColor: C.errorBg, border: `1px solid ${C.errorBdr}`, color: C.error, borderRadius: "8px", padding: "10px 14px", fontSize: "13px", marginBottom: "18px" }}>
            {serverError}
          </div>
        )}

        <form onSubmit={handleLogin} noValidate>
          <div style={{ marginBottom: "16px" }}>
            <label htmlFor="username" style={sharedStyles.label}>Username</label>
            <input
              id="username" type="text"
              style={sharedStyles.input(fieldErrors.username)}
              placeholder="Enter your username"
              value={username}
              onChange={(e) => {
                setUsername(e.target.value);
                if (fieldErrors.username) setFieldErrors(p => ({ ...p, username: "" }));
                if (serverError) setServerError("");
              }}
            />
            {fieldErrors.username && (
              <p style={{ fontSize: "11px", color: C.error, marginTop: "4px", marginBottom: 0 }}>{fieldErrors.username}</p>
            )}
          </div>

          <div style={{ marginBottom: "24px" }}>
            <label htmlFor="password" style={sharedStyles.label}>Password</label>
            <input
              id="password" type="password"
              style={sharedStyles.input(fieldErrors.password)}
              placeholder="Enter your password"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                if (fieldErrors.password) setFieldErrors(p => ({ ...p, password: "" }));
                if (serverError) setServerError("");
              }}
            />
            {fieldErrors.password && (
              <p style={{ fontSize: "11px", color: C.error, marginTop: "4px", marginBottom: 0 }}>{fieldErrors.password}</p>
            )}
          </div>

          <button type="submit" disabled={loading} style={sharedStyles.submitBtn(loading)}>
            {loading ? "Signing in…" : "Sign In →"}
          </button>
        </form>

        <div style={sharedStyles.footerText}>
          Don&apos;t have an account?
          <Link href="/register" style={{ color: C.mid, fontWeight: "600", textDecoration: "none", marginLeft: "4px" }}>
            Create one
          </Link>
        </div>
      </div>
    </AuthLayout>
  );
}