"use client";

import { useState, useEffect, useRef } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import api from "@/api/axios";
import { authSharedStyles } from "@/components/authStyles"; 

const styles = {
  ...authSharedStyles,
  container: {
    display: "flex",
    width: "100%",
    maxWidth: "1000px",
    minHeight: "620px",
    borderRadius: "8px",
    overflow: "hidden",
    boxShadow: "0 12px 48px rgba(0,0,0,0.12)",
  },

  rightPanel: { ...authSharedStyles.rightPanel, padding: "40px 40px", overflowY: "auto" },
  header: { marginBottom: "20px" },
  field: { marginBottom: "16px" },
  subtitle: { ...authSharedStyles.subtitle, marginBottom: "20px" },
  successBox: {
    backgroundColor: "#f4f4f4",
    border: "1px solid #d1d5db",
    color: "#1a1a1a",
    borderRadius: "8px",
    padding: "12px 14px",
    fontSize: "13px",
    marginBottom: "16px",
    textAlign: "center",
  },
  dropdownWrapper: { marginBottom: "16px" },
  dropdownBox: {
    width: "100%",
    padding: "12px 14px",
    border: "1px solid #d0d8e0",
    borderRadius: "4px",
    fontSize: "14px",
    outline: "none",
    boxSizing: "border-box",
    backgroundColor: "#ffffff",
    color: "#1a1a1a",
    cursor: "pointer",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  dropdownBoxError: { border: "1px solid #ff4444", backgroundColor: "#fff9f9" },
  dropdownMenu: {
    border: "1px solid #d0d8e0",
    borderRadius: "4px",
    marginTop: "6px",
    backgroundColor: "#ffffff",
    maxHeight: "180px",
    overflowY: "auto",
    boxShadow: "0 8px 20px rgba(0,0,0,0.08)",
  },
  dropdownItem: {
    padding: "12px 14px",
    fontSize: "14px",
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
    gap: "10px",
    color: "#1a1a1a",
  },
  dropdownItemHover: { backgroundColor: "#f5f5f5" },
  checkBox: { width: "14px", height: "14px", accentColor: "#000000" },
  tagRow: { display: "flex", flexWrap: "wrap", gap: "8px", marginTop: "10px" },
  tag: {
    display: "inline-flex",
    alignItems: "center",
    gap: "6px",
    backgroundColor: "#f0f2f5",
    color: "#1a1a1a",
    borderRadius: "999px",
    padding: "6px 10px",
    fontSize: "12px",
    fontWeight: "700",
  },
  tagRemove: { cursor: "pointer", fontSize: "13px", color: "#ff4444", lineHeight: 1 },
  rolesLoading: { fontSize: "12px", color: "#666666", padding: "10px 14px" },
};

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
  const dropdownRef = useRef(null);
  const boxRef = useRef(null);
  const [menuPos, setMenuPos] = useState({ top: 0, left: 0, width: 0 });

  useEffect(() => {
    async function fetchRoles() {
      try {
        const res = await api.get("/role/findByStatus");
        const data = res.data;
        setRoles(Array.isArray(data) ? data : data.data ?? []);
      } catch (err) {
        console.error(err);
        setRolesError("Could not load roles. Please refresh.");
      } finally {
        setRolesLoading(false);
      }
    }
    fetchRoles();
  }, []);

  useEffect(() => {
    function handleOutside(e) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setDropdownOpen(false);
      }
    }
    document.addEventListener("mousedown", handleOutside);
    return () => document.removeEventListener("mousedown", handleOutside);
  }, []);

  function recalculate() {
    if (boxRef.current) {
      const r = boxRef.current.getBoundingClientRect();
      const estimatedHeight = Math.min(roles.length * 38 || 38, 114);
      const spaceBelow = window.innerHeight - r.bottom;
      setMenuPos({ top: spaceBelow < estimatedHeight ? r.top - estimatedHeight : r.bottom, left: r.left, width: r.width });
    }
  }

  function handleChange(e) {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    if (fieldErrors[name]) setFieldErrors(prev => ({ ...prev, [name]: "" }));
  }

  function toggleRole(identifier) {
    setSelectedRoles(prev =>
      prev.includes(identifier)
        ? prev.filter(r => r !== identifier)
        : [...prev, identifier]
    );
    if (fieldErrors.roles) setFieldErrors(prev => ({ ...prev, roles: "" }));
  }

  function removeRole(identifier) {
    setSelectedRoles(prev => prev.filter(r => r !== identifier));
  }

  function validate() {
    const errors = {};
    if (!form.name.trim()) errors.name = "Full name is required.";
    if (!form.username.trim()) errors.username = "Email ID is required.";
    else if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.username.trim())) errors.username = "Please enter a valid email address.";
    if (!form.phoneNo.trim()) errors.phoneNo = "Phone number is required.";
    else if (!/^\d{10}$/.test(form.phoneNo.trim())) errors.phoneNo = "Phone number must be exactly 10 digits.";
    if (selectedRoles.length === 0) errors.roles = "Please select at least one role.";
    if (!form.password) errors.password = "Password is required.";
    else if (form.password.length < 4) errors.password = "Password must be at least 4 characters.";
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
        name: form.name,
        username: form.username,
        phoneNo: form.phoneNo,
        password: form.password,
        roles: selectedRoles,
      });
      const data = res.data;
      if (data.success === true) {
        setMessage({ text: "Registration successful! Redirecting to login…", type: "success" });
        setTimeout(() => router.push("/login"), 1500);
      } else {
        setMessage({ text: data.message || "Registration failed. Please try again.", type: "error" });
      }
    } catch (err) {
      const msg = err.response?.data?.message || "Server error. Please try again.";
      setMessage({ text: msg, type: "error" });
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
            Join RetailPOS and streamline your retail operations. Complete inventory management, fast checkouts, and powerful analytics.
          </p>
        </div>
        <div style={styles.rightPanel}>
          <div style={styles.header}>
            <h1 style={styles.title}>Register</h1>
            <p style={styles.subtitle}>Sign up for a RetailPOS account</p>
          </div>
          {message.text && (
            <div style={message.type === "success" ? styles.successBox : styles.errorBox}>
              {message.text}
            </div>
          )}
          <form onSubmit={handleRegister} noValidate>
            <div style={styles.field}>
              <label style={styles.label} htmlFor="name">Full Name</label>
              <input id="name" name="name" type="text"
                style={{ ...styles.input, ...(fieldErrors.name ? styles.inputError : {}) }}
                placeholder="e.g. John Smith" value={form.name} onChange={handleChange} />
              {fieldErrors.name && <p style={styles.errorText}>{fieldErrors.name}</p>}
            </div>
            <div style={styles.field}>
              <label style={styles.label} htmlFor="username">Email ID</label>
              <input id="username" name="username" type="email"
                style={{ ...styles.input, ...(fieldErrors.username ? styles.inputError : {}) }}
                placeholder="e.g. john@example.com" value={form.username}
                onChange={handleChange} autoComplete="email" />
              {fieldErrors.username && <p style={styles.errorText}>{fieldErrors.username}</p>}
            </div>
            <div style={styles.field}>
              <label style={styles.label} htmlFor="phoneNo">Phone Number</label>
              <input id="phoneNo" name="phoneNo" type="tel"
                style={{ ...styles.input, ...(fieldErrors.phoneNo ? styles.inputError : {}) }}
                placeholder="10-digit phone number" value={form.phoneNo} onChange={handleChange} />
              {fieldErrors.phoneNo && <p style={styles.errorText}>{fieldErrors.phoneNo}</p>}
            </div>
            <style>{`
              .sd2-wrap { width: 100%; position: relative; font-family: "Segoe UI", sans-serif; margin-bottom: 16px; }
              .sd2-label { font-size: 12px; font-weight: 600; color: #374151; margin-bottom: 0; margin-top: 0; display: block; letter-spacing: 0.2px; }
              .sd2-box {
                width: 100%; padding: 9px 12px;
                background: #fafafa; border: 1.5px solid #e8e8e8;
                border-radius: 7px; cursor: pointer;
                box-sizing: border-box; font-size: 13px;
                color: #1a1a1a; transition: border-color 0.15s;
                display: flex; align-items: center; justify-content: space-between;
              }
              .sd2-box:hover { border-color: #000000; }
              .sd2-box.open { border-color: #1a1a1a; }
              .sd2-box.error { border-color: #ff4444; background-color: #fff9f9; }
              .sd2-chevron { font-size: 10px; color: #999999; transition: transform 0.2s; }
              .sd2-chevron.open { transform: rotate(180deg); }
              .sd2-menu {
                position: fixed;
                background: #ffffff; border: 1.5px solid #e8e8e8;
                border-radius: 8px; margin-top: 4px;
                max-height: 114px; overflow-y: auto;
                box-shadow: 0 8px 24px rgba(0,0,0,0.08);
                z-index: 99999;
              }
              .sd2-item {
                width: 100%; padding: 9px 12px; display: flex;
                align-items: center; gap: 9px;
                cursor: pointer; border: none; border-bottom: 1px solid #f3f4f6;
                background: transparent;
                transition: background 0.1s; font-size: 13px; color: #374151;
                text-align: left;
              }
              .sd2-item:last-child { border-bottom: none; }
              .sd2-item:hover { background: #f5f5f5; }
              .sd2-item.selected { background: rgba(0,0,0,0.06); color: #000000; font-weight: 500; }
            `}</style>
            <div className="sd2-wrap" ref={dropdownRef}>
              <label className="sd2-label" htmlFor="roles-dropdown">Assign Role(s)</label>
              <button
                ref={boxRef}
                type="button"
                id="roles-dropdown"
                aria-expanded={dropdownOpen}
                aria-haspopup="listbox"
                className={`sd2-box${dropdownOpen ? " open" : ""}${fieldErrors.roles ? " error" : ""}`}
                onClick={() => { if (!dropdownOpen) { recalculate(); } setDropdownOpen(o => !o); }}
              >
                <span style={{ color: selectedRoles.length === 0 ? "#9ca3af" : "#1a1a1a" }}>
                  {selectedRoles.length === 0 ? "Select role(s)…" : `${selectedRoles.length} role(s) selected`}
                </span>
                <span className={`sd2-chevron${dropdownOpen ? " open" : ""}`}>▼</span>
              </button>
              {dropdownOpen && (
                <div
                  className="sd2-menu"
                  style={{ top: menuPos.top, left: menuPos.left, width: menuPos.width }}
                  aria-multiselectable="true"
                  aria-label="Assign roles"
                >
                  {rolesLoading && <div className="sd2-item">Loading roles…</div>}
                  {rolesError && <div className="sd2-item" style={{ color: "#ff4444" }}>{rolesError}</div>}
                  {!rolesLoading && !rolesError && roles.map(role => {
                    const isSelected = selectedRoles.includes(role.identifier);
                    return (
                      <button
                        key={role.identifier}
                        type="button"
                        className={`sd2-item${isSelected ? " selected" : ""}`}
                        aria-pressed={isSelected}
                        onClick={() => toggleRole(role.identifier)}
                      >
                        <span style={{ width: 18, display: "inline-block", textAlign: "center" }}>
                          {isSelected ? "✓" : ""}
                        </span>
                        {role.identifier}
                      </button>
                    );
                  })}
                </div>
              )}
              {selectedRoles.length > 0 && (
                <div style={styles.tagRow}>
                  {selectedRoles.map(r => (
                    <span key={r} style={styles.tag}>
                      {r}
                      <button
                        type="button"
                        style={styles.tagRemove}
                        aria-label={`Remove ${r}`}
                        onClick={() => removeRole(r)}
                        onKeyDown={(e) => {
                          if (e.key === "Enter" || e.key === " ") {
                            e.preventDefault();
                            removeRole(r);
                          }
                        }}
                      >
                        ×
                      </button>
                    </span>
                  ))}
                </div>
              )}
            </div>
            {fieldErrors.roles && (
              <p style={{ ...styles.errorText, marginTop: "-8px", marginBottom: "10px" }}>
                {fieldErrors.roles}
              </p>
            )}
            <div style={styles.field}>
              <label style={styles.label} htmlFor="password">Password</label>
              <input id="password" name="password" type="password"
                style={{ ...styles.input, ...(fieldErrors.password ? styles.inputError : {}) }}
                placeholder="Min. 4 characters" value={form.password}
                onChange={handleChange} autoComplete="new-password" />
              {fieldErrors.password && <p style={styles.errorText}>{fieldErrors.password}</p>}
            </div>
            <button type="submit"
              className="register-button"
              style={{ ...styles.button, ...(loading ? styles.buttonDisabled : {}) }}
              disabled={loading}>
              {loading ? "Registering…" : "Register"}
            </button>
          </form>
          <div style={styles.footer}>
            Already have an account?
            <Link href="/login" style={styles.link}>Sign in</Link>
          </div>
        </div>
      </div>
    </div>
  );
}