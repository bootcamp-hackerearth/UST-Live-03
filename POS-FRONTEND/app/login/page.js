"use client";

import React, { useState } from "react";
import PropTypes from "prop-types";
import axios from "axios";
import { useRouter } from "next/navigation";

const inputBaseStyle = {
  width: "100%",
  paddingTop: "12px",
  paddingBottom: "12px",
  fontSize: "14px",
  backgroundColor: "#ffffff",
  border: "1px solid #e5e5e5",
  borderRadius: "6px",
  color: "#111111",
  outline: "none",
  boxSizing: "border-box",
  transition: "all 0.2s ease-in-out",
};

const iconStyle = {
  position: "absolute",
  left: "14px",
  top: "50%",
  transform: "translateY(-50%)",
  width: "16px",
  height: "16px",
  pointerEvents: "none",
};

const focusHandler = (e) => {
  e.target.style.borderColor = "#111111";
  e.target.style.backgroundColor = "#fafafa";
};

const blurHandler = (e) => {
  e.target.style.borderColor = "#e5e5e5";
  e.target.style.backgroundColor = "#ffffff";
};

function InputField({
  id,
  name,
  type,
  placeholder,
  value,
  onChange,
  icon,
  rightElement,
}) {
  return (
    <div style={{ position: "relative" }}>
      {icon}
      <input
        id={id}
        name={name}
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        style={{
          ...inputBaseStyle,
          paddingLeft: "42px",
          paddingRight: rightElement ? "44px" : "14px",
        }}
        onFocus={focusHandler}
        onBlur={blurHandler}
        required
      />
      {rightElement}
    </div>
  );
}

InputField.propTypes = {
  id: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  type: PropTypes.string,
  placeholder: PropTypes.string,
  value: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  icon: PropTypes.node,
  rightElement: PropTypes.node,
};

function Login() {
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
      setError("Please enter username and password.");
      return;
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
    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#fafafa",
        padding: "24px 16px",
        fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
      }}
    >
      <div
        style={{
          width: "100%",
          maxWidth: "380px",
          backgroundColor: "#ffffff",
          borderRadius: "12px",
          border: "1px solid #e5e5e5",
          padding: "40px 32px",
          boxSizing: "border-box",
        }}
      >
        <div style={{ marginBottom: "32px" }}>
          <h2 style={{ fontSize: "20px", fontWeight: 600, color: "#111111", letterSpacing: "-0.3px", marginBottom: "6px" }}>
            Sign In
          </h2>
          <p style={{ color: "#737373", fontSize: "13px" }}>
            Enter your credentials to access your terminal dashboard.
          </p>
        </div>

        {error && (
          <div
            style={{
              backgroundColor: "#fafafa",
              border: "1px solid #e5e5e5",
              borderRadius: "6px",
              padding: "12px",
              marginBottom: "20px",
              color: "#111111",
              fontSize: "13px",
              textAlign: "left",
            }}
          >
            • {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: "18px" }}>
            <label htmlFor="username" style={{ display: "block", marginBottom: "6px", fontSize: "12px", fontWeight: 500, color: "#737373" }}>
              Email address
            </label>
            <InputField
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

          <div style={{ marginBottom: "24px" }}>
            <label htmlFor="password" style={{ display: "block", marginBottom: "6px", fontSize: "12px", fontWeight: 500, color: "#737373" }}>
              Password
            </label>
            <InputField
              id="password"
              name="password"
              type={showPassword ? "text" : "password"}
              placeholder="••••••••"
              value={credentials.password}
              onChange={handleChange}
              icon={
                <svg style={iconStyle} viewBox="0 0 24 24" fill="none" stroke="#a3a3a3" strokeWidth="1.5">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 002.25-2.25v-6.75a2.25 2.25 0 00-2.25-2.25H6.75a2.25 2.25 0 00-2.25 2.25v6.75a2.25 2.25 0 002.25 2.25z" />
                </svg>
              }
                rightElement={
            <button
              type="button"
              onClick={() => setShowPassword((p) => !p)}
              style={{
                position: "absolute",
                right: "12px",
                top: "50%",
                transform: "translateY(-50%)",
                border: "none",
                background: "none",
                cursor: "pointer",
              }}
            >
            {showPassword ? (
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#737373" strokeWidth="1.5">
              <path d="M1 12s4-6 11-6 11 6 11 6-4 6-11 6-11-6-11-6z"/>
              <circle cx="12" cy="12" r="3"/>
            </svg>
            ) : (
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#737373" strokeWidth="1.5">
              <path d="M17.94 17.94A10.94 10.94 0 0112 19c-7 0-11-7-11-7a21.13 21.13 0 015.06-5.94"/>
              <path d="M1 1l22 22"/>
            </svg>
            )}
          </button>
              }
            />
          </div>

         
          <button
            type="submit"
            disabled={isLoading}
            style={{
              width: "100%",
              padding: "12px",
              backgroundColor: "#111111",
              color: "#ffffff",
              border: "none",
              borderRadius: "6px",
              fontWeight: 500,
              fontSize: "14px",
              cursor: isLoading ? "not-allowed" : "pointer",
              transition: "background-color 0.15s ease",
            }}
            onMouseEnter={(e) => !isLoading && (e.target.style.backgroundColor = "#262626")}
            onMouseLeave={(e) => !isLoading && (e.target.style.backgroundColor = "#111111")}
          >
            {isLoading ? "Verifying..." : "Continue"}
          </button>
        </form>
        
        <p style={{ marginTop: "24px", textAlign: "center", fontSize: "13px", color: "#737373" }}>
          Don’t have an account?{" "}
          <button
            onClick={() => router.push("/register")}
            style={{
              border: "none",
              background: "none",
              color: "#111111",
              fontWeight: 500,
              cursor: "pointer",
              textDecoration: "underline",
              padding: 0,
            }}
          >
            Register
          </button>
        </p>  
      </div>
    </div>
  );
}

export default Login;