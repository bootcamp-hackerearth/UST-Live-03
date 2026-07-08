"use client";

import { useState, useEffect } from "react";
import axios from "axios";
import { useRouter } from "next/navigation";
import { getListItems } from "@/services/api";
import Select from "react-select";
import "./Register.css";

function Register() {
  const router = useRouter();

  const [username, setUsername] = useState("");
  const [name, setName] = useState("");
  const [message, setMessage] = useState("");
  const [phoneNo, setPhoneNo] = useState("");
  const [role, setRole] = useState([]);
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

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setMessage("");

    // Role is optional
    if (
      !username.trim() ||
      !name.trim() ||
      !phoneNo.trim() ||
      !password.trim()
    ) {
      setError("Please fill all required fields");
      return;
    }

    if (phoneNo.length !== 10) {
      setError("Phone number must be exactly 10 digits");
      return;
    }

    try {
      const res = await axios.post("http://15.134.213.85:8080/register", {
        username,
        name,
        phoneNo,
        roles: role, // [] if nothing is selected
        password,
      });

      const data = res?.data;

      if (data?.success === false) {
        setMessage(data.message);
        return;
      }

      setMessage(data?.message || "Registration Successful");

      setTimeout(() => {
        router.push("/login");
      }, 1500);
    } catch (error) {
      console.error(error);
      setMessage(
        error?.response?.data?.message ||
          error?.message ||
          "Registration Failed"
      );
    }
  };

  const roleOptions = roles.map((r) => ({
    label: r.identifier,
    value: r.identifier,
  }));

  return (
    <div className="registerContainer">
      <form onSubmit={handleSubmit} className="registerCard">
        <h2 className="registerTitle">Create Account</h2>

        {message && <div className="message">{message}</div>}
        {error && <div className="error">{error}</div>}

        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          className="registerInput"
        />

        <input
          type="text"
          placeholder="Full Name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="registerInput"
        />

        <input
          type="text"
          placeholder="Phone Number"
          value={phoneNo}
          maxLength={10}
          onChange={(e) => {
            const value = e.target.value.replace(/\D/g, "");
            setPhoneNo(value.slice(0, 10));
          }}
          className="registerInput"
        />

        <div className="roleSelect">
          <Select
            instanceId="roles-select"
            isMulti
            isSearchable
            isLoading={loadingRoles}
            classNamePrefix="react-select"
            placeholder="Select Roles (Optional)"
            options={roleOptions}
            value={roleOptions.filter((option) =>
              role.includes(option.value)
            )}
            onChange={(selected) =>
              setRole(selected ? selected.map((item) => item.value) : [])
            }
          />
        </div>

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="registerInput"
        />

        <button type="submit" className="registerButton">
          Register
        </button>

        <p className="linkText">
          Already have an account?{" "}
          <button
            type="button"
            className="link"
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
