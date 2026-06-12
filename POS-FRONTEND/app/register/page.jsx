"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import api from "@/api/axios";
import AuthLayout, { C, sharedStyles } from "@/components/AuthLayout";

export default function Register() {
  const [form, setForm] = useState({ name: "", username: "", phoneNo: "", password: "" });
  const [selectedRoles, setSelectedRoles] = useState([]);
  const [fieldErrors, setFieldErrors] = useState({});
  const [message, setMessage] = useState({ text: "", type: "" });
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const [roles, setRoles] = useState([]);
  const [rolesLoading, setRolesLoading] = useState(true);
  const [rolesError, setRolesError] = useState("");
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [hoveredRole, setHoveredRole] = useState(null);

  useEffect(() => {
    async function fetchRoles() {
      try {
        const res = await api.get("/role/findByStatus");
        const data = res.data;
        setRoles(Array.isArray(data) ? data : data.data ?? []);
      } catch (err) {
        if (process.env.NODE_ENV !== "production") console.error(err);
        setRolesError("Could not load roles. Please refresh.");
      } finally {
        setRolesLoading(false);
      }
    }
    fetchRoles();
  }, []);

  useEffect(() => {
    function handleClickOutside(e) {
      if (!e.target.closest("#roles-dropdown-wrapper")) setDropdownOpen(false);
    }
    if (dropdownOpen) document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [dropdownOpen]);

  function handleChange(e) {
    const { name, value } = e.target;
    const computedValue = name === "phoneNo" ? value.replaceAll(/\D/g, "") : value;
    setForm(prev => ({ ...prev, [name]: computedValue }));
    if (fieldErrors[name]) setFieldErrors(prev => ({ ...prev, [name]: "" }));
  }

  function toggleRole(identifier) {
    setSelectedRoles(prev => prev.includes(identifier) ? prev.filter(r => r !== identifier) : [...prev, identifier]);
    if (fieldErrors.roles) setFieldErrors(prev => ({ ...prev, roles: "" }));
  }

  function validate() {
    const errors = {};
    if (!form.name.trim()) errors.name = "Full name is required.";
    
      const emailRegex = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;    // NOSONAR                       
    if (!form.username.trim()) {
      errors.username = "Username/Email is required.";
    } else if (!form.username.includes("@")) {
      errors.username = "Username must contain an '@' symbol.";
    } else if (!emailRegex.test(form.username.trim())) {
      errors.username = "Provide a valid email layout (e.g., user@domain.com).";
    }

    if (!form.phoneNo.trim()) {
      errors.phoneNo = "Phone number is required.";
    } else if (!/^\d{10}$/.test(form.phoneNo.trim())) {
      errors.phoneNo = "Must be exactly 10 digits.";
    }

    if (selectedRoles.length === 0) errors.roles = "Select at least one role.";
    if (!form.password) {
      errors.password = "Password is required.";
    } else if (form.password.length < 4) {
      errors.password = "Min. 4 characters.";
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  }

  async function handleRegister(e) {
    e.preventDefault();
    if (!validate()) return;
    setLoading(true);
    setMessage({ text: "", type: "" });
    try {
      const res = await api.post("/user/register", {
        name: form.name, username: form.username, phoneNo: form.phoneNo, password: form.password, roles: selectedRoles,
      });
      const data = res.data;
      if (data.success === false) {
        setMessage({ text: data.message || "User with this identifier already exists.", type: "error" });
        return;
      }
      if (data.identifier || data.username || data.success === true) {
        setMessage({ text: "Registration successful! Redirecting to login…", type: "success" });
        setTimeout(() => router.push("/login"), 1500);
      } else {
        setMessage({ text: data.message || "Registration failed. Please try again.", type: "error" });
      }
    } catch (err) {
      const status = err.response?.status;
      const msg = err.response?.data?.message;
      if (status === 409 || status === 400) {
        setMessage({ text: msg || "User with this identifier already exists.", type: "error" });
      } else {
        setMessage({ text: msg || "Server error. Please try again.", type: "error" });
      }
    } finally {
      setLoading(false);
    }
  }

  const errorText = { fontSize: "10px", color: C.error, marginTop: "2px", marginBottom: 0 };

  return (
    <AuthLayout maxWidth="480px">
      <div style={{ padding: "20px 32px 22px" }}>
        <div style={{ marginBottom: "16px" }}>
          <h2 style={{ margin: "0 0 2px", fontSize: "15px", fontWeight: "700", color: C.navy }}>Create account</h2>
          <p style={{ margin: 0, fontSize: "12px", color: C.muted }}>Register a new RetailPOS user</p>
        </div>

        {message.text && (
          <div style={{
            backgroundColor: message.type === "success" ? C.successBg : C.errorBg,
            border: `1px solid ${message.type === "success" ? C.successBdr : C.errorBdr}`,
            color: message.type === "success" ? C.successText : C.error,
            borderRadius: "7px", padding: "7px 11px", fontSize: "12px", marginBottom: "13px",
          }}>
            {message.text}
          </div>
        )}

        <form onSubmit={handleRegister} noValidate>
          <div style={{ display: "flex", gap: "12px", marginBottom: "11px" }}>
            <div style={{ flex: 1, minWidth: 0 }}>
              <label style={sharedStyles.label} htmlFor="name">Full Name</label>
              <input id="name" name="name" type="text" style={sharedStyles.input(fieldErrors.name)} placeholder="John Smith" value={form.name} onChange={handleChange} />
              {fieldErrors.name && <p style={errorText}>{fieldErrors.name}</p>}
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <label style={sharedStyles.label} htmlFor="username">Username (Email)</label>
              <input id="username" name="username" type="email" style={sharedStyles.input(fieldErrors.username)} placeholder="john@example.com" value={form.username} onChange={handleChange} autoComplete="username" />
              {fieldErrors.username && <p style={errorText}>{fieldErrors.username}</p>}
            </div>
          </div>

          <div style={{ display: "flex", gap: "12px", marginBottom: "11px" }}>
            <div style={{ flex: 1, minWidth: 0 }}>
              <label style={sharedStyles.label} htmlFor="phoneNo">Phone Number</label>
              <input id="phoneNo" name="phoneNo" type="text" inputMode="numeric" style={sharedStyles.input(fieldErrors.phoneNo)} placeholder="Digits only" value={form.phoneNo} onChange={handleChange} />
              {fieldErrors.phoneNo && <p style={errorText}>{fieldErrors.phoneNo}</p>}
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <label style={sharedStyles.label} htmlFor="password">Password</label>
              <input id="password" name="password" type="password" style={sharedStyles.input(fieldErrors.password)} placeholder="Min. 4 chars" value={form.password} onChange={handleChange} autoComplete="new-password" />
              {fieldErrors.password && <p style={errorText}>{fieldErrors.password}</p>}
            </div>
          </div>

          <div id="roles-dropdown-wrapper" style={{ marginBottom: "18px", position: "relative" }}>
            <label style={sharedStyles.label} htmlFor="roles-dropdown-toggle">Assign Role(s)</label>
            <button
              id="roles-dropdown-toggle" type="button" onClick={() => setDropdownOpen(o => !o)} aria-expanded={dropdownOpen} aria-haspopup="listbox" style={sharedStyles.input(fieldErrors.roles)}
            >
              <span style={{ color: selectedRoles.length === 0 ? C.muted : C.text }}>
                {selectedRoles.length === 0 ? "Select role(s)…" : `${selectedRoles.length} role(s) selected`}
              </span>
              <span style={{ fontSize: "10px", color: C.muted }}>{dropdownOpen ? "▲" : "▼"}</span>
            </button>

            {dropdownOpen && (
              <div style={{ position: "absolute", top: "100%", left: 0, right: 0, zIndex: 999, border: `1.5px solid ${C.gray}`, borderRadius: "8px", marginTop: "3px", backgroundColor: C.white, maxHeight: "130px", overflowY: "auto", boxShadow: "0 6px 18px rgba(54,57,85,0.15)" }}>
                {rolesLoading && <div style={{ padding: "8px 12px", fontSize: "12px", color: C.muted }}>Loading roles…</div>}
                {rolesError && <div style={{ padding: "8px 12px", fontSize: "12px", color: C.error }}>{rolesError}</div>}
                {!rolesLoading && !rolesError && roles.map(role => (
                  <button
                    key={role.identifier} type="button" onClick={() => toggleRole(role.identifier)} onMouseEnter={() => setHoveredRole(role.identifier)} onMouseLeave={() => setHoveredRole(null)}
                    style={{ width: "100%", textAlign: "left", padding: "7px 12px", fontSize: "13px", cursor: "pointer", display: "flex", alignItems: "center", gap: "8px", color: C.text, backgroundColor: hoveredRole === role.identifier ? "rgba(84,102,142,0.07)" : "transparent", border: "none", outline: "none" }}
                  >
                    <span style={{ width: "14px", height: "14px", display: "inline-flex", alignItems: "center", justifyContent: "center", border: `1.5px solid ${selectedRoles.includes(role.identifier) ? C.mid : C.gray}`, borderRadius: "3px", background: selectedRoles.includes(role.identifier) ? "rgba(84,102,142,0.12)" : "transparent", color: C.mid }}>
                      {selectedRoles.includes(role.identifier) ? "✓" : ""}
                    </span>
                    <span>{role.identifier}</span>
                  </button>
                ))}
              </div>
            )}
            {fieldErrors.roles && <p style={{ ...errorText, marginTop: "4px" }}>{fieldErrors.roles}</p>}
          </div>

          <button type="submit" disabled={loading} style={sharedStyles.submitBtn(loading)}>
            {loading ? "Registering…" : "Register User →"}
          </button>
        </form>

        <div style={sharedStyles.footerText}>
          Already have an account?
          <Link href="/login" style={{ color: C.mid, fontWeight: "600", textDecoration: "none", marginLeft: "4px" }}>
            Sign in
          </Link>
        </div>
      </div>
    </AuthLayout>
  );
}