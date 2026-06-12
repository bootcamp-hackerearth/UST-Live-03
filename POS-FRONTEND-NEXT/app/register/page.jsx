"use client";

import { useState, useEffect } from "react";
import axios from "axios";
import { useRouter } from "next/navigation";
import { getListItems } from "@/services/api";

function Register() {
  const router = useRouter();

  const [username, setUsername] = useState("");
  const [name, setName] = useState("");
  const [phoneNo, setPhoneNo] = useState("");
  const [role, setRole] = useState([]);
  const [password, setPassword] = useState("");

  const [roles, setRoles] = useState([]);
  const [loadingRoles, setLoadingRoles] = useState(true);
  const [error, setError] = useState("");

  // ✅ Fetch roles
  useEffect(() => {
    const fetchRoles = async () => {
      try {
        console.log("Fetching roles...");
        const response = await getListItems("role");
        console.log("Roles API:", response);
        setRoles(response || []);
      } catch (err) {
        console.error("Error fetching roles:", err);
        setError("Failed to load roles");
      } finally {
        setLoadingRoles(false);
      }
    };

    fetchRoles();
  }, []);

  // ✅ Submit
  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");

    // Required field validation
    if (
      !username.trim() ||
      !name.trim() ||
      !phoneNo.trim() ||
      role.length === 0 ||
      !password.trim()
    ) {
      setError("Please fill all fields");
      return;
    }

    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailRegex.test(username.trim())) {
      setError("Username must be a valid email address");
      return;
    }

    // Phone number validation
    const phoneRegex = /^\d{10}$/;

    if (!phoneRegex.test(phoneNo.trim())) {
      setError("Phone number must be exactly 10 digits");
      return;
    }

    try {
      await axios.post("http://localhost:8080/register", {
        username,
        name,
        phoneNo,
        roles: role,
        password,
      });

      alert("Registration Successful ✅");

      router.push("/login");
    } catch (error) {
      console.log(error);
      setError("Registration Failed ❌");
    }
  };

  return (
    <div style={styles.container}>
      <form onSubmit={handleSubmit} style={styles.card}>
        <h2 style={styles.title}>Create Account</h2>

        {/* Error */}
        {error && <p style={styles.error}>{error}</p>}

        <input
          type="text"
          placeholder="Username (Email)"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          style={styles.input}
        />

        <input
          type="text"
          placeholder="Full Name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          style={styles.input}
        />

        <input
          type="text"
          placeholder="Phone Number"
          value={phoneNo}
          onChange={(e) =>
            setPhoneNo(e.target.value.replaceAll(/\D/g, ""))
          }
          maxLength={10}
          style={styles.input}
        />

        {/* ✅ Roles Dropdown */}
        <select
          multiple
          value={role}
          onChange={(e) =>
            setRole(
              [...e.target.selectedOptions].map((o) => o.value)
            )
          }
          style={styles.input}
        >
          <option value="" disabled>
            {loadingRoles ? "Loading roles..." : "Select Roles"}
          </option>

          {!loadingRoles &&
  roles.map((r) => (
    <option
      key={r.identifier}
      value={r.identifier}
    >
      {r.identifier}
    </option>
  ))}
        </select>

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          style={styles.input}
        />

        <button type="submit" style={styles.button}>
          Register
        </button>

        <p style={styles.linkText}>
          Already have an account?{" "}
          <button
  type="button"
  style={{
    ...styles.link,
    background: "none",
    border: "none",
    padding: 0,
    cursor: "pointer",
  }}
  onClick={() => router.push("/login")}
>
  Login
</button>
        </p>
      </form>
    </div>
  );
}

export default Register;

// ✅ Styling
const styles = {
  container: {
    minHeight: "100vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    background: "#f8fafc",
    fontFamily: "'Inter', sans-serif",
    padding: "20px",
    position: "relative",
    overflow: "hidden",
  },

  bgCircle1: {
    position: "absolute",
    width: "350px",
    height: "350px",
    borderRadius: "50%",
    background: "rgba(99,102,241,0.15)",
    top: "-120px",
    left: "-100px",
    filter: "blur(30px)",
  },

  bgCircle2: {
    position: "absolute",
    width: "300px",
    height: "300px",
    borderRadius: "50%",
    background: "rgba(139,92,246,0.15)",
    bottom: "-100px",
    right: "-80px",
    filter: "blur(30px)",
  },

  card: {
    width: "100%",
    maxWidth: "430px",
    background: "rgba(255,255,255,0.9)",
    backdropFilter: "blur(10px)",
    padding: "35px",
    borderRadius: "24px",
    display: "flex",
    flexDirection: "column",
    gap: "16px",
    boxShadow: "0 10px 40px rgba(0,0,0,0.08)",
    border: "1px solid rgba(255,255,255,0.4)",
    position: "relative",
    zIndex: 10,
  },

  logoSection: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    marginBottom: "10px",
  },

  logoCircle: {
    width: "70px",
    height: "70px",
    borderRadius: "18px",
    background: "linear-gradient(135deg, #6366f1, #8b5cf6)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    color: "#fff",
    fontWeight: "700",
    fontSize: "28px",
    marginBottom: "15px",
    boxShadow: "0 10px 25px rgba(99,102,241,0.3)",
  },

  title: {
    textAlign: "center",
    margin: 0,
    fontSize: "30px",
    fontWeight: "700",
    color: "#111827",
  },

  subtitle: {
    textAlign: "center",
    color: "#6b7280",
    marginTop: "6px",
    fontSize: "14px",
  },

  inputGroup: {
    display: "flex",
    flexDirection: "column",
    gap: "6px",
  },

  label: {
    fontSize: "13px",
    fontWeight: "600",
    color: "#374151",
  },

  input: {
    padding: "14px",
    borderRadius: "14px",
    border: "1px solid #e5e7eb",
    fontSize: "14px",
    outline: "none",
    transition: "0.3s",
    background: "#fff",
    color: "#111827",
    boxSizing: "border-box",
  },

  select: {
    padding: "14px",
    borderRadius: "14px",
    border: "1px solid #e5e7eb",
    fontSize: "14px",
    outline: "none",
    background: "#fff",
    minHeight: "120px",
    color: "#111827",
  },

  button: {
    padding: "14px",
    border: "none",
    borderRadius: "14px",
    background: "linear-gradient(135deg, #6366f1, #8b5cf6)",
    color: "#fff",
    fontSize: "15px",
    fontWeight: "700",
    cursor: "pointer",
    marginTop: "10px",
    transition: "0.3s ease",
    boxShadow: "0 10px 20px rgba(99,102,241,0.25)",
  },

  error: {
    background: "#fee2e2",
    color: "#dc2626",
    padding: "12px",
    borderRadius: "12px",
    textAlign: "center",
    fontSize: "14px",
    border: "1px solid #fecaca",
  },

  linkText: {
    textAlign: "center",
    fontSize: "14px",
    color: "#6b7280",
    marginTop: "10px",
  },

  link: {
    color: "#6366f1",
    cursor: "pointer",
    fontWeight: "600",
  },
};