"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import axios from "axios";

function Register() {
  const router = useRouter();

  const [username, setUsername] = useState("");
  const [name, setName] = useState("");
  const [phoneNo, setPhoneNo] = useState("");
  const [selectedRoles, setSelectedRoles] = useState([]);
  const [password, setPassword] = useState("");

  const [roles, setRoles] = useState([]);
  const [loadingRoles, setLoadingRoles] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchRoles = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/role/list"
        );

        setRoles(response.data || []);
      } catch {
        setError("Failed to load roles");
      } finally {
        setLoadingRoles(false);
      }
    };

    fetchRoles();
  }, []);

  const handleRoleChange = (e) => {
    const values = Array.from(
      e.target.selectedOptions,
      (option) => option.value
    );

    setSelectedRoles(values);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (!username.trim()) {
  setError("Username is required");
  return;
}

if (!name.trim()) {
  setError("Name is required");
  return;
}

if (!phoneNo.trim()) {
  setError("Phone number is required");
  return;
}

if (selectedRoles.length === 0) {
  setError("Please select at least one role");
  return;
}

if (!password.trim()) {
  setError("Password is required");
  return;
}

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

if (!emailRegex.test(username)) {
  setError("Enter a valid email address");
  return;
}

if (!/^\d{10}$/.test(phoneNo)) {
  setError("Phone number must contain exactly 10 digits");
  return;
}

const passwordRegex =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&^#]).{8,}$/;

if (!passwordRegex.test(password)) {
  setError(
    "Password must contain 8+ characters, uppercase, lowercase, number and special character"
  );
  return;
}

    try {
      const response = await axios.post(
        "http://localhost:8080/register",
        {
          username,
          name,
          phoneNo,
          roles: selectedRoles,
          password,
        }
      );
 
      console.log(
        "REGISTER RESPONSE:",
        response.data
      );
 
      if (response.data?.success === false) {
        setError(
          response.data.message ||
            "User already exists"
        );
        return;
      }
 
      alert("Registration Successful ");
 
      router.push("/login");
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Registration Failed "
      );
    }
  };

  return (
    <div style={styles.container}>
      <form onSubmit={handleSubmit} style={styles.card}>
        <h2 style={styles.title}>Register</h2>

        <input
          type="text"
          placeholder="Username"
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
          maxLength={10}
          onChange={(e) => {
            const value = e.target.value.replaceAll(/\D/g, "");
            setPhoneNo(value);
          }}
          style={styles.input}
        />

        <select
          multiple
          value={selectedRoles}
          onChange={handleRoleChange}
          style={styles.multiSelect}
        >
          {loadingRoles ? (
            <option>Loading roles...</option>
          ) : (
            roles.map((r) => (
              <option key={r.identifier} value={r.identifier}>
                {r.identifier}
              </option>
            ))
          )}
        </select>

        <p style={styles.helperText}>
          Hold Ctrl (Windows) or Cmd (Mac) to select multiple roles
        </p>

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          style={styles.input}
        />

        {error && <p style={styles.error}>{error}</p>}

        <button type="submit" style={styles.button}>
          Register
        </button>

        <p style={styles.text}>
          Already have account?{" "}
          <Link href="/login" style={styles.link}>
            Login
          </Link>
        </p>
      </form>
    </div>
  );
}

export default Register;

const styles = {
  container: {
    minHeight: "100vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    background: "#f8fafc",
    padding: "20px",
  },

  card: {
    width: "100%",
    maxWidth: "520px",
    background: "#fff",
    padding: "38px",
    borderRadius: "24px",
    display: "flex",
    flexDirection: "column",
    gap: "18px",
    boxShadow: "0 10px 40px rgba(0,0,0,0.08)",
  },

  title: {
    fontSize: "42px",
    fontWeight: "700",
    color: "#111827",
    textAlign: "center",
    marginBottom: "10px",
  },

  input: {
    padding: "16px",
    borderRadius: "14px",
    border: "1px solid #e5e7eb",
    fontSize: "15px",
    outline: "none",
    color: "#111827",
  },

  multiSelect: {
    height: "120px",
    padding: "14px",
    borderRadius: "14px",
    border: "1px solid #e5e7eb",
    fontSize: "15px",
    outline: "none",
    color: "#111827",
  },

  helperText: {
    color: "#6b7280",
    fontSize: "13px",
    marginTop: "-8px",
  },

  button: {
    padding: "16px",
    border: "none",
    borderRadius: "16px",
    background: "linear-gradient(135deg, #6366f1, #8b5cf6)",
    color: "#fff",
    fontSize: "20px",
    fontWeight: "700",
    cursor: "pointer",
    marginTop: "8px",
    boxShadow: "0 10px 25px rgba(99,102,241,0.3)",
  },

  error: {
    background: "#fee2e2",
    color: "#dc2626",
    padding: "12px",
    borderRadius: "12px",
    textAlign: "center",
  },

  text: {
    textAlign: "center",
    color: "#6b7280",
    fontSize: "16px",
  },

  link: {
    color: "#6366f1",
    textDecoration: "none",
    fontWeight: "600",
  },
};