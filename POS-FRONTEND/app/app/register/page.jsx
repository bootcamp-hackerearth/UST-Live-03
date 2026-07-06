"use client";
import { useState, useEffect } from "react";

function Register() {
  const [form, setForm] = useState({
    username: "",
    name: "",
    phoneNo: "",
    password: "",
    roles: [],
  });
  const [rolesList, setRolesList] = useState([]);
  const [message, setMessage] = useState("");

  useEffect(() => {
    fetchRoles();
  }, []);

  async function fetchRoles() {
    try {
      const response = await fetch("/api/role/list", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ page: 0, sizePerPage: 100 }),
      });
      const data = await response.json();
      setRolesList(data.dtoList || []);
    } catch (err) {
      console.error(err);
      setMessage("Failed to load roles");
    }
  }

  function handleChange(e) {
    const { name, value } = e.target;
    if (name === "phoneNo") {
      const numbersOnly = value.replaceAll(/\D/g, "");
      setForm({ ...form, phoneNo: numbersOnly.slice(0, 10) });
      return;
    }
    setForm({ ...form, [name]: value });
  }

  function handleRoleChange(e) {
    const values = Array.from(
      e.target.selectedOptions,
      (option) => option.value,
    );
    setForm({ ...form, roles: values });
  }

  async function handleRegister(e) {
    e.preventDefault();
    if (!form.name.trim()) {
      setMessage("Name is required");
      return;
    }
    if (form.name.trim().length < 3) {
      setMessage("Name must contain at least 3 characters");
      return;
    }
    if (!/^[A-Za-z ]+$/.test(form.name.trim())) {
      setMessage("Name should contain only letters");
      return;
    }
    if (!form.username.trim()) {
      setMessage("Username is required");
      return;
    }
    if (form.username.trim().length < 4) {
      setMessage("Username must contain at least 4 characters");
      return;
    }
    if (!/^\d{10}$/.test(form.phoneNo)) {
      setMessage("Phone number must be exactly 10 digits");
      return;
    }
    const passwordRegex =
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#^()_+\-=])[A-Za-z\d@$!%*?&#^()_+\-=]{8,}$/;
    if (!passwordRegex.test(form.password)) {
      setMessage(
        "Password must contain uppercase, lowercase, number, special character and minimum 8 characters",
      );
      return;
    }
    if (form.roles.length === 0) {
      setMessage("Please select at least one role");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/user/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });

      const text = await response.text();
      let data;
      try {
        data = JSON.parse(text);
      } catch {
        data = {};
      }

      if (response.ok && data.success !== false) {
        setMessage("User Registered");
        setTimeout(() => {
          globalThis.location.href = "/login";
        }, 1000);
      } else {
        setMessage(data.message || "Registration Failed");
      }
    } catch (err) {
      console.error(err);
      setMessage("Server Error");
    }
  }

  return (
    <div
      style={{
        height: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        background: "#f5f5f5",
      }}
    >
      <form
        onSubmit={handleRegister}
        style={{
          width: "400px",
          padding: "25px",
          borderRadius: "10px",
          background: "white",
          boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
        }}
      >
        <h2 style={{ marginBottom: "15px" }}>Register</h2>

        {message && (
          <p
            style={{
              color: message === "User Registered" ? "green" : "red",
              fontSize: "14px",
            }}
          >
            {message}
          </p>
        )}

        <input
          type="text"
          name="name"
          placeholder="Name"
          value={form.name}
          onChange={handleChange}
          style={{ width: "100%", padding: "10px", marginBottom: "10px" }}
        />

        <input
          type="text"
          name="username"
          placeholder="Username"
          value={form.username}
          onChange={handleChange}
          style={{ width: "100%", padding: "10px", marginBottom: "10px" }}
        />

        <input
          type="text"
          name="phoneNo"
          placeholder="Phone Number"
          value={form.phoneNo}
          onChange={handleChange}
          maxLength={10}
          style={{ width: "100%", padding: "10px", marginBottom: "10px" }}
        />

        <input
          type="password"
          name="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
          style={{ width: "100%", padding: "10px", marginBottom: "10px" }}
        />

        <select
          multiple
          onChange={handleRoleChange}
          style={{
            width: "100%",
            padding: "10px",
            marginBottom: "10px",
            minHeight: "100px",
          }}
        >
          {rolesList.map((role) => (
            <option key={role.identifier} value={role.identifier}>
              {role.identifier}
            </option>
          ))}
        </select>

        <button
          type="submit"
          style={{
            width: "100%",
            padding: "10px",
            background: "black",
            color: "white",
            border: "none",
            cursor: "pointer",
            marginBottom: "10px",
          }}
        >
          Register
        </button>

        <button
          type="button"
          onClick={() => (globalThis.location.href = "/login")}
          style={{ width: "100%", padding: "10px", cursor: "pointer" }}
        >
          Back
        </button>
      </form>
    </div>
  );
}
export default Register;
