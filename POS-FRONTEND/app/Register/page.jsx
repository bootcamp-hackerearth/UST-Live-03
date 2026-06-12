"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";

function Register() {
  const router = useRouter();

  const [formData, setFormData] = useState({
    name: "",
    username: "",
    roles: [],
    phoneNo: "",
    password: "",
  });

  const [rolesError, setRolesError] = useState("");
  const [availableRoles, setAvailableRoles] = useState([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchRoles = async () => {
      setRolesError("");

      try {
        const response = await fetch(
          "http://localhost:8080/api/role/findActiveStatus",
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        const responseText = await response.text();
        const data = responseText ? JSON.parse(responseText) : [];

        if (!response.ok) {
          throw new Error(data.message || "Unable to load roles");
        }

        const activeRoles = Array.isArray(data)
          ? data.filter((role) => role.status !== false)
          : [];

        setAvailableRoles(activeRoles);
      } catch (fetchRolesError) {
        console.error(fetchRolesError);
        setRolesError(
          fetchRolesError.message || "Unable to load roles"
        );
      }
    };

    fetchRoles();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: name === "username" ? value.toLowerCase() : value,
    }));
  };

  const handleRoleChange = (e) => {
    const selectedRoles = Array.from(
      e.target.selectedOptions,
      (option) => option.value
    );

    setFormData((prev) => ({
      ...prev,
      roles: selectedRoles,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setLoading(true);
    setMessage("");

    try {
      const response = await axios.post(
        "http://localhost:8080/api/user/register",
        formData
      );

      console.log("Registration Response:", response.data);

      if (response.data.success === false) {
        setMessage(response.data.message);
        return;
      }

      alert("Registration Successful");

      router.push("/Login");
    } catch (error) {
      console.error(error);

      setMessage(
        error.response?.data?.message ||
          "Registration Failed"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        background: "#ffffff",
        padding: "20px",
      }}
    >
      <div
        style={{
          width: "100%",
          maxWidth: "450px",
          background: "#ffffff",
          padding: "35px",
          borderRadius: "16px",
          boxShadow: "0 20px 45px rgba(0,0,0,0.15)",
        }}
      >
        <h2
          style={{
            textAlign: "center",
            color: "#000",
            marginBottom: "25px",
          }}
        >
          User Registration
        </h2>

        {message && (
          <div
            style={{
              background: "#ffe5e5",
              color: "#d10000",
              padding: "12px",
              borderRadius: "8px",
              marginBottom: "15px",
              textAlign: "center",
            }}
          >
            {message}
          </div>
        )}

        {rolesError && (
          <div
            style={{
              background: "#ffe5e5",
              color: "#d10000",
              padding: "12px",
              borderRadius: "8px",
              marginBottom: "15px",
              textAlign: "center",
            }}
          >
            {rolesError}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={fieldStyle}>
            <label htmlFor="name" style={labelStyle}>
              Name
            </label>

            <input
              type="text"
              name="name"
              required
              value={formData.name}
              onChange={handleChange}
              style={inputStyle}
            />
          </div>

          <div style={fieldStyle}>
            <label htmlFor="email" style={labelStyle}>
              Email
            </label>

            <input
              type="email"
              name="username"
              required
              value={formData.username}
              onChange={handleChange}
              style={inputStyle}
            />
          </div>

          <div style={fieldStyle}>
            <label htmlFor="roles" style={labelStyle}>
              Roles
            </label>

            <select
              multiple
              required
              value={formData.roles}
              onChange={handleRoleChange}
              style={{
                ...inputStyle,
                height: "120px",
              }}
            >
              {availableRoles.map((role) => (
                <option
                  key={role.id}
                  value={role.identifier}
                >
                  {role.identifier}
                </option>
              ))}
            </select>
          </div>

          <div style={fieldStyle}>
            <label htmlFor="phoneNo" style={labelStyle}>
              Phone Number
            </label>

            <input
              type="text"
              name="phoneNo"
              required
              pattern="^[0-9]{10}$"
              title="Phone number must be exactly 10 digits"
              value={formData.phoneNo}
              onChange={handleChange}
              style={inputStyle}
            />
          </div>

          <div style={fieldStyle}>
            <label htmlFor="password" style={labelStyle}>
              Password
            </label>

            <input
              type="password"
              name="password"
              required
              value={formData.password}
              onChange={handleChange}
              style={inputStyle}
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            style={{
              width: "100%",
              padding: "13px",
              background:
                "linear-gradient(135deg,#4b6cb7,#182848)",
              color: "#fff",
              border: "none",
              borderRadius: "10px",
              fontSize: "16px",
              fontWeight: "600",
              cursor: loading ? "not-allowed" : "pointer",
              opacity: loading ? 0.7 : 1,
            }}
          >
            {loading ? "Registering..." : "Register"}
          </button>

          <button
            type="button"
            onClick={() => router.push("/Login")}
            style={{
              width: "100%",
              marginTop: "12px",
              padding: "13px",
              background: "#ffffff",
              color: "#000",
              border: "1px solid #ccc",
              borderRadius: "10px",
              fontSize: "16px",
              cursor: "pointer",
            }}
          >
            Back to Login
          </button>
        </form>
      </div>
    </div>
  );
}

const fieldStyle = {
  marginBottom: "16px",
};

const labelStyle = {
  display: "block",
  marginBottom: "6px",
  color: "#000",
  fontWeight: "600",
};

const inputStyle = {
  width: "100%",
  padding: "12px",
  borderRadius: "8px",
  border: "1px solid #ccc",
  fontSize: "14px",
  color: "#000",
  backgroundColor: "#fff",
  boxSizing: "border-box",
};

export default Register;