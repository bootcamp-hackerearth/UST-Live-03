"use client";

import React, { useState, useEffect } from "react";
import axios from "axios";
import { useRouter } from "next/navigation";
import Link from "next/link";

/* =========================
   Reusable Minimalist Styles
========================= */
const inputBaseStyle = {
  width: "100%",
  paddingTop: "8px",
  paddingBottom: "8px",
  fontSize: "13px",
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
  left: "12px",
  top: "50%",
  transform: "translateY(-50%)",
  width: "14px",
  height: "14px",
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

/* =========================
   Registration Component
========================= */
function Register() {
  const [rolesList, setRolesList] = useState([]);
  const [error, setError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const router = useRouter();

  const [user, setUser] = useState({
    identifier: "",
    name: "",
    username: "",
    password: "",
    phoneNo: "",
    roles: [],
  });

  useEffect(() => {
    fetchRoles();
  }, []);

  const fetchRoles = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/role/findByStatus");
      setRolesList(response.data.content || response.data);
    } catch (err) {
      console.error("Error fetching operational security roles", err);
    }
  };

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
    setError("");
  };

  const toggleRole = (roleName) => {
    const updatedRoles = user.roles.includes(roleName)
      ? user.roles.filter((r) => r !== roleName)
      : [...user.roles, roleName];
    setUser({ ...user, roles: updatedRoles });
    setError("");
  };

  const validate = () => {
    if (user.roles.length === 0) return "Select at least one terminal security role";
    if (!/^\d{10}$/.test(user.phoneNo)) return "Phone number must be exactly 10 digits";
    if (user.password.length < 6) return "Password must be at least 6 characters long";
    if (!/[A-Z]/.test(user.password)) return "Add at least one uppercase letter (A-Z)";
    if (!/\d/.test(user.password)) return "Add at least one numerical digit (0-9)";
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const validationError = validate();
    if (validationError) return setError(validationError);

    try {
      const res = await axios.post("http://localhost:8080/api/user/add", user);
      if (res.data.success === false) return setError(res.data.message);
      setError("");
      setSuccessMsg("Account provisions initialized. Redirecting...");
      setTimeout(() => router.push("/"), 1500);
    } catch (err) {
      console.error("Registration rejected by gateway server", err);
      setError("Registration rejected by gateway server");
    }
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        width: "100%",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#fafafa",
        padding: "12px",
        fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
        boxSizing: "border-box",
      }}
    >
      <div
        style={{
          width: "100%",
          maxWidth: "480px",
          backgroundColor: "#ffffff",
          borderRadius: "10px",
          border: "1px solid #e5e5e5",
          padding: "20px 24px",
          boxSizing: "border-box",
        }}
      >
        {/* Logo Header */}
        <div style={{ display: "flex", alignItems: "center", gap: "6px", marginBottom: "12px" }}>
          <div style={{ width: "20px", height: "20px", backgroundColor: "#111111", borderRadius: "4px", display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0 }}>
            <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="#ffffff" strokeWidth="2.5">
              <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 10.5V6a3.75 3.75 0 1 0-7.5 0v4.5m11.356-1.993 1.263 12c.07.665-.45 1.243-1.119 1.243H4.25a1.125 1.125 0 0 1-1.12-1.243l1.264-12A1.125 1.125 0 0 1 5.513 7.5h12.974c.576 0 1.059.435 1.119 1.007Z" />
            </svg>
          </div>
          <span style={{ fontSize: "10px", fontWeight: 600, letterSpacing: "0.05em", textTransform: "uppercase", color: "#737373" }}>
            RetailOS POS
          </span>
        </div>

        <h2 style={{ fontSize: "16px", fontWeight: 600, color: "#111111", letterSpacing: "-0.3px", margin: "0 0 2px 0" }}>Register identity</h2>
        <p style={{ fontSize: "12px", color: "#737373", margin: "0 0 16px 0" }}>Enter operator details to provision an account.</p>
        
        {/* Messages */}
        {error && (
          <div style={{ backgroundColor: "#fafafa", border: "1px solid #e5e5e5", color: "#111111", fontSize: "12px", borderRadius: "6px", padding: "8px", marginBottom: "12px" }}>
            • {error}
          </div>
        )}
        {successMsg && (
          <div style={{ backgroundColor: "#fafafa", border: "1px solid #e5e5e5", color: "#111111", fontSize: "12px", borderRadius: "6px", padding: "8px", marginBottom: "12px" }}>
            ✓ {successMsg}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
          
          {/* Two Column Grid Inputs */}
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px" }}>
            {/* Full Name */}
            <div>
              <label htmlFor="register-name" style={{ display: "block", fontSize: "11px", fontWeight: 500, color: "#737373", marginBottom: "4px" }}>
                Full Name
              </label>
              <div style={{ position: "relative" }}>
                <svg style={iconStyle} viewBox="0 0 24 24" fill="none" stroke="#a3a3a3" strokeWidth="1.5">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z" />
                </svg>
                <input
                  id="register-name"
                  name="name"
                  type="text"
                  placeholder="John Doe"
                  value={user.name}
                  onChange={handleChange}
                  required
                  style={{ ...inputBaseStyle, paddingLeft: "34px", paddingRight: "10px" }}
                  onFocus={focusHandler}
                  onBlur={blurHandler}
                />
              </div>
            </div>     

            {/* Email */}
            <div>
              <label htmlFor="register-email" style={{ display: "block", fontSize: "11px", fontWeight: 500, color: "#737373", marginBottom: "4px" }}>
                Email address
              </label>
              <div style={{ position: "relative" }}>
                <svg style={iconStyle} viewBox="0 0 24 24" fill="none" stroke="#a3a3a3" strokeWidth="1.5">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75" />
                </svg>
                <input
                  id="register-email"
                  name="username"
                  type="email"
                  placeholder="operator@retailos.com"
                  value={user.username}
                  onChange={handleChange}
                  required
                  style={{ ...inputBaseStyle, paddingLeft: "34px", paddingRight: "10px" }}
                  onFocus={focusHandler}
                  onBlur={blurHandler}
                />
              </div>
            </div>

            {/* Phone */}
            <div>
              <label htmlFor="register-phoneNo" style={{ display: "block", fontSize: "11px", fontWeight: 500, color: "#737373", marginBottom: "4px" }}>
                Contact Number
              </label>
              <div style={{ position: "relative" }}>
                <svg style={iconStyle} viewBox="0 0 24 24" fill="none" stroke="#a3a3a3" strokeWidth="1.5">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M10.5 1.5H8.25A2.25 2.25 0 0 0 6 3.75v16.5a2.25 2.25 0 0 0 2.25 2.25h7.5A2.25 2.25 0 0 0 18 20.25V3.75a2.25 2.25 0 0 0-2.25-2.25H13.5m-3 0V3h3V1.5m-3 0h3m-3 18.75h3" />
                </svg>
                <input
                  id="register-phoneNo"
                  name="phoneNo"
                  type="text"
                  placeholder="10-digit number"
                  value={user.phoneNo}
                  onChange={handleChange}
                  required
                  style={{ ...inputBaseStyle, paddingLeft: "34px", paddingRight: "10px" }}
                  onFocus={focusHandler}
                  onBlur={blurHandler}
                />
              </div>
            </div>

            {/* Password */}
            <div>
              <label htmlFor="register-password" style={{ display: "block", fontSize: "11px", fontWeight: 500, color: "#737373", marginBottom: "4px" }}>
                Password
              </label>
              <div style={{ position: "relative" }}>
                <svg style={iconStyle} viewBox="0 0 24 24" fill="none" stroke="#a3a3a3" strokeWidth="1.5">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 0 0 2.25-2.25v-6.75a2.25 2.25 0 0 0-2.25-2.25H6.75a2.25 2.25 0 0 0-2.25 2.25v6.75a2.25 2.25 0 0 0 2.25 2.25z" />
                </svg>
                <input
                  id="register-password"
                  name="password"
                  type={showPassword ? "text" : "password"}
                  placeholder="••••••••"
                  value={user.password}
                  onChange={handleChange}
                  required
                  style={{ ...inputBaseStyle, paddingLeft: "34px", paddingRight: "36px" }}
                  onFocus={focusHandler}
                  onBlur={blurHandler}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  style={{ position: "absolute", right: "10px", top: "50%", transform: "translateY(-50%)", background: "none", border: "none", cursor: "pointer", padding: "2px", color: "#737373", display: "flex", alignItems: "center" }}
                >
                  <span style={{ fontSize: "10px", textDecoration: "underline" }}>{showPassword ? "hide" : "show"}</span>
                </button>
              </div>
            </div>
          </div>
          
          {/* Custom Minimalist Checkbox Grid Roles Selector */}
          <fieldset style={{ border: "none", margin: 0, padding: 0 }}>
            <legend style={{ display: "block", fontSize: "11px", fontWeight: 500, color: "#737373", marginBottom: "4px" }}>
              Assign Terminal Roles
            </legend>
            <div style={{ display: "flex", flexDirection: "column", gap: "4px", maxHeight: "85px", overflowY: "auto", paddingRight: "2px" }}>
              {rolesList.map((role) => {
                const isActive = user.roles.includes(role.identifier);
                return (
                  <button
                    key={role.id || role.identifier}
                    type="button"
                    onClick={() => toggleRole(role.identifier)}
                    aria-pressed={isActive}
                    style={{
                      padding: "6px 10px",
                      border: "1px solid #e5e5e5",
                      borderRadius: "6px",
                      backgroundColor: isActive ? "#fafafa" : "#ffffff",
                      cursor: "pointer",
                      display: "flex",
                      gap: "10px",
                      alignItems: "center",
                      textAlign: "left",
                      transition: "all 0.15s ease",
                    }}
                  >
                    <span style={{
                      width: "12px",
                      height: "12px",
                      borderRadius: "3px",
                      border: `1.5px solid ${isActive ? "#111111" : "#d4d4d4"}`,
                      backgroundColor: isActive ? "#111111" : "#ffffff",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      flexShrink: 0,
                    }}>
                      {isActive && (
                        <svg width="6" height="6" viewBox="0 0 10 10" fill="none">
                          <polyline points="1.5,5 4,7.5 8.5,2.5" stroke="#ffffff" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" />
                        </svg>
                      )}
                    </span>
                    <span style={{ flex: 1, minWidth: 0 }}>
                      <p style={{ fontSize: "12px", fontWeight: 500, color: "#111111", margin: 0, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                        {role.identifier}
                      </p>
                    </span>
                  </button>
                );
              })}
            </div>
          </fieldset>

          {/* Submit Button */}
          <button
            type="submit"
            style={{
              width: "100%",
              backgroundColor: "#111111",
              color: "#ffffff",
              fontSize: "13px",
              fontWeight: 500,
              padding: "9px",
              borderRadius: "6px",
              border: "none",
              cursor: "pointer",
              marginTop: "2px",
              transition: "background-color 0.15s ease",
            }}
            onMouseEnter={e => e.target.style.backgroundColor = "#262626"}
            onMouseLeave={e => e.target.style.backgroundColor = "#111111"}
          >
            Sign up
          </button>
        </form>

        <div style={{ height: "1px", backgroundColor: "#e5e5e5", margin: "12px 0" }} />

        {/* Footer Redirect Links */}
        <p style={{ textAlign: "center", fontSize: "12px", color: "#737373", margin: 0 }}>
          Already have an account?{" "}
          <Link href="/login" style={{ color: "#111111", fontWeight: 500, textDecoration: "underline" }}>
            Sign in
          </Link>
        </p>

        <p style={{ textAlign: "center", fontSize: "10px", color: "#a3a3a3", marginTop: "10px", marginBottom: 0 }}>
          © 2026 Pod1-RetailOS • v4.2.1
        </p>
      </div>
    </div>
  );
}

export default Register;