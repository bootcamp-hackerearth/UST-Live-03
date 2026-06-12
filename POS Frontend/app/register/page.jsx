"use client";

import { useState, useEffect } from "react";
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
  dropdownBoxError: { borderColor: "#ff4444", backgroundColor: "#fff9f9" },
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
  const [hoveredRole, setHoveredRole] = useState(null);

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
    if (!form.username.trim()) errors.username = "Username is required.";
    if (!form.phoneNo.trim()) errors.phoneNo = "Phone number is required.";
    else if (!/^\d{7,15}$/.test(form.phoneNo.trim())) errors.phoneNo = "Phone must be 7–15 digits only.";
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
              <label style={styles.label} htmlFor="username">Username</label>
              <input id="username" name="username" type="text"
                style={{ ...styles.input, ...(fieldErrors.username ? styles.inputError : {}) }}
                placeholder="e.g. johnsmith" value={form.username}
                onChange={handleChange} autoComplete="username" />
              {fieldErrors.username && <p style={styles.errorText}>{fieldErrors.username}</p>}
            </div>
            <div style={styles.field}>
              <label style={styles.label} htmlFor="phoneNo">Phone Number</label>
              <input id="phoneNo" name="phoneNo" type="tel"
                style={{ ...styles.input, ...(fieldErrors.phoneNo ? styles.inputError : {}) }}
                placeholder="7-15 digits" value={form.phoneNo} onChange={handleChange} />
              {fieldErrors.phoneNo && <p style={styles.errorText}>{fieldErrors.phoneNo}</p>}
            </div>
            <div style={styles.dropdownWrapper}>
              <label style={styles.label} htmlFor="roles-dropdown">Assign Role(s)</label>
              <button
                type="button"
                id="roles-dropdown"
                aria-expanded={dropdownOpen}
                aria-controls="roles-dropdown-list"
                aria-label="Assign roles"
                style={{ ...styles.dropdownBox, ...(fieldErrors.roles ? styles.dropdownBoxError : {}) }}
                onClick={() => setDropdownOpen(o => !o)}
              >
                <span style={{ color: selectedRoles.length === 0 ? "#999" : "#1a1a1a", fontSize: "13px" }}>
                  {selectedRoles.length === 0 ? "Select role(s)…" : `${selectedRoles.length} role(s) selected`}
                </span>
                <span style={{ fontSize: "11px", color: "#999" }}>{dropdownOpen ? "▲" : "▼"}</span>
              </button>
              {dropdownOpen && (
                <div id="roles-dropdown-list" style={styles.dropdownMenu} aria-multiselectable="true">
                  {rolesLoading && <div style={styles.rolesLoading}>Loading roles…</div>}
                  {rolesError && <div style={{ ...styles.rolesLoading, color: "#ff4444" }}>{rolesError}</div>}
                  {!rolesLoading && !rolesError && roles.map(role => (
                    <button
                      key={role.identifier}
                      type="button"
                      aria-pressed={selectedRoles.includes(role.identifier)}
                      style={{
                        ...styles.dropdownItem,
                        ...(hoveredRole === role.identifier ? styles.dropdownItemHover : {}),
                      }}
                      onClick={() => toggleRole(role.identifier)}
                      onMouseEnter={() => setHoveredRole(role.identifier)}
                      onMouseLeave={() => setHoveredRole(null)}
                    >
                      <input
                        type="checkbox"
                        style={styles.checkBox}
                        checked={selectedRoles.includes(role.identifier)}
                        onChange={() => toggleRole(role.identifier)}
                        onClick={e => e.stopPropagation()}
                        tabIndex={-1}
                      />
                      {role.identifier}
                    </button>
                  ))}
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