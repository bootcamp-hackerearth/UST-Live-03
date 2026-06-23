"use client";

import React, { useState } from "react";
import axios from "axios";
import { useRouter } from "next/navigation";
import AuthInputField from "@/app/components/auth/authInputField"
import { containerStyle, cardStyle, iconStyle } from "../components/auth/authstyle";

export default function Login() {
  const [credentials, setCredentials] = useState({ username: "", password: "" });
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const router = useRouter();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCredentials((prev) => ({ ...prev, [name]: value }));
    if (error) setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!credentials.username.trim() || !credentials.password.trim()) {
      return setError("Please enter username and password.");
    }

    setIsLoading(true);
    setError("");

    try {
      const response = await axios.post("http://localhost:8080/api/authenticate", credentials);
      const token = response?.data?.token;

      if (!token || token === "null") {
        setError("Authentication failed. Invalid token entry.");
        return;
      }

      localStorage.setItem("token", token);
      localStorage.setItem("username", credentials.username);
      document.cookie = `token=${token}; path=/`;
      document.cookie = `username=${credentials.username}; path=/`;

      router.push("/home");
    } catch (err) {
      setError(err?.response?.data?.message || "Invalid credentials entry code.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={containerStyle}>
      <div style={{ ...cardStyle, maxWidth: "390px" }}>
        <div style={{ marginBottom: "24px" }}>
          <h2 style={{ fontSize: "20px", fontWeight: 600, color: "#111111", marginBottom: "6px" }}>Sign In</h2>
          <p style={{ color: "#737373", fontSize: "13px" }}>Enter your credentials to access your dashboard.</p>
        </div>

        {error && (
          <div style={{ backgroundColor: "#fafafa", border: "1px solid #e5e5e5", borderRadius: "6px", padding: "12px", marginBottom: "16px", color: "#111111", fontSize: "13px" }}>
            • {error}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
          <div>
            <label htmlFor="username" style={{ display: "block", marginBottom: "6px", fontSize: "12px", fontWeight: 500, color: "#737373" }}>
              Email address
            </label>
            <AuthInputField
              id="username"
              name="username"
              type="email"
              placeholder="name@domain.com"
              value={credentials.username}
              onChange={handleChange}
              icon={
                <svg style={iconStyle} viewBox="0 0 24 24" fill="none" stroke="#a3a3a3" strokeWidth="1.5">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75" />
                </svg>
              }
            />
          </div>

          <div>
            <label htmlFor="password" style={{ display: "block", marginBottom: "6px", fontSize: "12px", fontWeight: 500, color: "#737373" }}>
              Password
            </label>
            <AuthInputField
              id="password"
              name="password"
              type={showPassword ? "text" : "password"}
              placeholder="••••••••"
              value={credentials.password}
              onChange={handleChange}
              icon={
                <svg style={iconStyle} viewBox="0 0 24 24" fill="none" stroke="#a3a3a3" strokeWidth="1.5">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 0 0 2.25-2.25v-6.75a2.25 2.25 0 0 0-2.25-2.25H6.75a2.25 2.25 0 0 0-2.25 2.25v6.75a2.25 2.25 0 0 0 2.25 2.25z" />
                </svg>
              }
              rightElement={
                <button
                  type="button"
                  onClick={() => setShowPassword((p) => !p)}
                  style={{ position: "absolute", right: "12px", top: "50%", transform: "translateY(-50%)", border: "none", background: "none", cursor: "pointer" }}
                >
                  <span style={{ fontSize: "11px", textDecoration: "underline", color: "#737373" }}>{showPassword ? "hide" : "show"}</span>
                </button>
              }
            />
          </div>

          <button
            type="submit"
            disabled={isLoading}
            style={{
              width: "100%",
              padding: "11px",
              backgroundColor: "#111111",
              color: "#ffffff",
              border: "none",
              borderRadius: "6px",
              fontWeight: 500,
              fontSize: "14px",
              cursor: isLoading ? "not-allowed" : "pointer",
            }}
          >
            {isLoading ? "Verifying..." : "Continue"}
          </button>
        </form>

        <p style={{ marginTop: "20px", textAlign: "center", fontSize: "13px", color: "#737373" }}>
          Don’t have an account?{" "}
          <button onClick={() => router.push("/register")} style={{ border: "none", background: "none", color: "#111111", fontWeight: 500, cursor: "pointer", textDecoration: "underline" }}>
            Register
          </button>
        </p>
      </div>
    </div>
  );
}