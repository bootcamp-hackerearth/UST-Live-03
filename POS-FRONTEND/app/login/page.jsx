"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import axios from "axios";

const Login = () => {
  const router = useRouter();
  const [credentials, setCredentials] = useState({ username: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    axios.post("http://localhost:8080/api/authenticate", credentials, {
      withCredentials: true,
    })
      .then((res) => {
        if (!res.data || res.data.token === "Error") {
          setError("Incorrect username or password");
          setLoading(false);
          return;
        }

        const fetchedRole = res.data.roles?.[0];
        const plainRoleString = typeof fetchedRole === "object" && fetchedRole !== null
          ? fetchedRole.identifier || fetchedRole.name
          : fetchedRole || "USER";

        localStorage.setItem("token", res.data.token);
        localStorage.setItem("name", res.data.name);
        localStorage.setItem("username", res.data.username);
        localStorage.setItem("role", plainRoleString);
        localStorage.setItem("phoneNo", res.data.phoneNo || "");
        router.push("/home");
      })
      .catch((err) => {
        console.log("ERROR", err);
        setError(err.response?.data?.message || err.message);
        setLoading(false);
      });
  };

  const inputStyle = {
    width: "100%", height: "48px", background: "#f8f8fc", border: "1.5px solid #ebebf5",
    borderRadius: "10px", padding: "0 16px", fontSize: "14px", color: "#2d2d6e",
    outline: "none", boxSizing: "border-box", transition: "all 0.15s ease"
  };

  return (
    <div style={{ minHeight: "100vh", backgroundColor: "#6c63ff", display: "flex", alignItems: "center", justifyContent: "center", fontFamily: "-apple-system, sans-serif", padding: "24px", boxSizing: "border-box" }}>
      <div style={{ width: "100%", maxWidth: "1000px", minHeight: "600px", background: "#f4f5fa", borderRadius: "24px", display: "grid", gridTemplateColumns: "1.1fr 0.9fr", overflow: "hidden", boxShadow: "0 20px 50px rgba(0,0,0,0.15)" }}>

        <div style={{ background: "#ffffff", display: "flex", flexDirection: "column", justifyContent: "space-between", padding: "48px 56px" }}>
          <div>
            <Link href="/" style={{ fontSize: "20px", fontWeight: "700", color: "#2d2d6e", textDecoration: "none", letterSpacing: "-0.5px" }}>POS<span style={{ color: "#6c63ff" }}>APK</span></Link>
          </div>

          <div style={{ width: "100%", maxWidth: "340px", margin: "auto 0" }}>
            <h1 style={{ fontSize: "24px", fontWeight: "600", color: "#2d2d6e", marginBottom: "8px" }}>Login to your account</h1>
            <p style={{ fontSize: "14px", color: "#8888a0", marginBottom: "32px" }}>Securely access your merchant dashboard.</p>

            <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "20px" }}>
              <div>
                <label htmlFor="username" style={{ display: "block", fontSize: "13px", fontWeight: "500", color: "#4b4b75", marginBottom: "6px" }}>Email address</label>
                <input id="username" type="text" name="username" style={inputStyle} value={credentials.username} onChange={handleChange} placeholder="name@company.com" required />
              </div>

              <div>
                <label htmlFor="password" style={{ display: "block", fontSize: "13px", fontWeight: "500", color: "#4b4b75", marginBottom: "6px" }}>Password</label>
                <div style={{ position: "relative" }}>
                  <input id="password" type={showPassword ? "text" : "password"} name="password" style={{ ...inputStyle, paddingRight: "48px" }} value={credentials.password} onChange={handleChange} placeholder="••••••••" required />
                  <button type="button" style={{ position: "absolute", right: "14px", top: "50%", transform: "translateY(-50%)", background: "none", border: "none", fontSize: "12px", color: "#8888a0", cursor: "pointer", fontWeight: "500" }} onClick={() => setShowPassword(!showPassword)}>
                    {showPassword ? "Hide" : "Show"}
                  </button>
                </div>
              </div>

              {error && <div style={{ backgroundColor: "#fff2f2", border: "1px solid #ffd6d6", color: "#e55555", fontSize: "13px", borderRadius: "8px", padding: "10px 12px", textAlign: "center" }}>{error}</div>}

              <button type="submit" disabled={loading} style={{ width: "100%", height: "48px", background: "#6c63ff", border: "none", borderRadius: "10px", color: "#ffffff", fontSize: "14px", fontWeight: "500", cursor: loading ? "not-allowed" : "pointer", opacity: loading ? 0.6 : 1 }}>
                {loading ? "Signing in..." : "Log in"}
              </button>
            </form>

            <p style={{ fontSize: "13px", color: "#8888a0", marginTop: "24px" }}>Don't have an account? <Link href="/register" style={{ color: "#6c63ff", textDecoration: "none", fontWeight: "600" }}>Register</Link></p>
          </div>

          <div style={{ fontSize: "12px", color: "#b0b0c8" }}>© POS 2026</div>
        </div>

        <div style={{ background: "#f4f5fa", display: "flex", flexDirection: "column", justifyContent: "space-between", padding: "48px" }}>
          <div style={{ display: "flex", justifyContent: "flex-end" }}>
            <Link href="/register" style={{ background: "#ffffff", border: "none", borderRadius: "20px", padding: "8px 20px", fontSize: "13px", fontWeight: "500", color: "#4b4b75", cursor: "pointer", boxShadow: "0 2px 10px rgba(0,0,0,0.04)", textDecoration: "none" }}>
              Register
            </Link>
          </div>

          <div style={{ display: "flex", gap: "20px", fontSize: "12px" }}>
            <a href="/terms" style={{ color: "#8888a0", textDecoration: "none" }}>Terms & Condition</a>
            <a href="/privacy" style={{ color: "#8888a0", textDecoration: "none" }}>Privacy Policy</a>
            <a href="/help" style={{ color: "#8888a0", textDecoration: "none" }}>Help</a>
          </div>
        </div>

      </div>
    </div>
  );
};

export default Login;