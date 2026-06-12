"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import axios from "axios";
import { getListItems } from "@/services/api";

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
        const response = await getListItems("role");
        setRoles(response || []);
      } catch (err) {
        console.error(err);
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

    if (
      !username ||
      !name ||
      !phoneNo ||
      selectedRoles.length === 0 ||
      !password
    ) {
      setError("Please fill all fields");
      return;
    }

    if (!/^\d{10}$/.test(phoneNo)) {
      setError(
        "Phone number must contain exactly 10 digits"
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

      alert("Registration Successful ✅");

      router.push("/login");
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Registration Failed ❌"
      );
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.bgCircle1}></div>

      <div style={styles.bgCircle2}></div>

      <form
        onSubmit={handleSubmit}
        style={styles.card}
      >
        <h2 style={styles.title}>Register</h2>

        {error && (
          <p style={styles.error}>{error}</p>
        )}

        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) =>
            setUsername(e.target.value)
          }
          style={styles.input}
        />

        <input
          type="text"
          placeholder="Full Name"
          value={name}
          onChange={(e) =>
            setName(e.target.value)
          }
          style={styles.input}
        />

        <input
          type="text"
          placeholder="Phone Number"
          value={phoneNo}
          maxLength={10}
          onChange={(e) => {
            const value =
              e.target.value.replace(/\D/g, "");
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
            roles.map((role) => (
              <option
                key={role.identifier}
                value={role.identifier}
              >
                {role.identifier}
              </option>
            ))
          )}
        </select>

        <p style={styles.helperText}>
          Hold Ctrl (Windows) or Cmd (Mac) to
          select multiple roles
        </p>

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) =>
            setPassword(e.target.value)
          }
          style={styles.input}
        />

        <button
          type="submit"
          style={styles.button}
        >
          Register
        </button>

        <p style={styles.text}>
          Already have account?{" "}
          <Link
            href="/login"
            style={styles.link}
          >
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
    background:
      "rgba(99,102,241,0.15)",
    top: "-120px",
    left: "-100px",
    filter: "blur(30px)",
  },

  bgCircle2: {
    position: "absolute",
    width: "300px",
    height: "300px",
    borderRadius: "50%",
    background:
      "rgba(139,92,246,0.15)",
    bottom: "-100px",
    right: "-80px",
    filter: "blur(30px)",
  },

  card: {
    width: "100%",
    maxWidth: "420px",
    background:
      "rgba(255,255,255,0.9)",
    backdropFilter: "blur(10px)",
    padding: "38px",
    borderRadius: "24px",
    display: "flex",
    flexDirection: "column",
    gap: "18px",
    boxShadow:
      "0 10px 40px rgba(0,0,0,0.08)",
    border:
      "1px solid rgba(255,255,255,0.4)",
    position: "relative",
    zIndex: 10,
  },

  title: {
    margin: 0,
    fontSize: "30px",
    fontWeight: "700",
    color: "#111827",
    textAlign: "center",
  },

  input: {
    padding: "14px",
    borderRadius: "14px",
    border: "1px solid #e5e7eb",
    fontSize: "14px",
    outline: "none",
    background: "#fff",
    color: "#111827",
    boxSizing: "border-box",
  },

  multiSelect: {
    height: "110px",
    padding: "14px",
    borderRadius: "14px",
    border: "1px solid #e5e7eb",
    fontSize: "14px",
    outline: "none",
    background: "#fff",
    color: "#111827",
    boxSizing: "border-box",
  },

  helperText: {
    color: "#4b5563",
    fontSize: "12px",
    marginTop: "-10px",
  },

  button: {
    padding: "14px",
    border: "none",
    borderRadius: "14px",
    background:
      "linear-gradient(135deg, #6366f1, #8b5cf6)",
    color: "#fff",
    fontSize: "15px",
    fontWeight: "700",
    cursor: "pointer",
    marginTop: "8px",
    transition: "0.3s ease",
    boxShadow:
      "0 10px 20px rgba(99,102,241,0.25)",
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

  text: {
    textAlign: "center",
    marginTop: "8px",
    color: "#4b5563",
    fontSize: "14px",
  },

  link: {
    color: "#6366f1",
    textDecoration: "none",
    fontWeight: "600",
  },
};