"use client";

import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  UserIcon,
  EnvelopeIcon,
  PhoneIcon,
  LockClosedIcon,
  ShieldCheckIcon,
  ArrowLeftIcon,
} from "@heroicons/react/24/outline";
import { useRouter } from "next/navigation";

const Register = () => {
  const router = useRouter();
  const [user, setUser] = useState({ name: "", username: "", roles: "", phoneNo: "", password: "" });
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const [roles, setRoles] = useState([]);

  useEffect(() => {
    axios.post(process.env.NEXT_PUBLIC_BASE_URL+"/role/list", { page: 0, sizePerPage: 10 })
      .then((res) => setRoles(res.data.dtoList || res.data || []))
      .catch((err) => console.error("Roles fetch error:", err));
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === "phoneNo") {
      const onlyNums = value.replaceAll(/\D/g, "");
      if (onlyNums.length > 10) return;
      setUser({ ...user, [name]: onlyNums });
      return;
    }
    setUser({ ...user, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setSuccess("");
    setError("");

    if (!/^[\w.-]+@[\w.-]+\.\w+$/.test(user.username)) return setError("Enter valid email");
    if (!/^\d{10}$/.test(user.phoneNo)) return setError("Phone number must be exactly 10 digits");
    if (!/^(?=.*[A-Za-z])(?=.*\d).{6,}$/.test(user.password)) {
      return setError("Password must be at least 6 characters long and contain both letters and numbers");
    }

    const formData = new URLSearchParams(user);
    axios.post(process.env.NEXT_PUBLIC_BASE_URL+"/register", formData, {
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
    })
      .then((res) => {
        if (res.data.success === false) return setError(res.data.message || "Registration failed");
        setSuccess("Registration successful");
        setUser({ name: "", username: "", roles: "", phoneNo: "", password: "" });
        setTimeout(() => router.push("/login"), 1200);
      })
      .catch((err) => {
        if (err.response?.status === 403) return setError("Registration failed: Email already exists or access is forbidden.");
        setError(typeof err.response?.data?.message === "string" ? err.response.data.message : "Registration failed due to a server error");
      });
  };

  const inputStyle = {
    width: "100%", height: "46px", background: "#f8f8fc", border: "1.5px solid #ebebf5",
    borderRadius: "10px", padding: "0 16px 0 44px", fontSize: "14px", color: "#2d2d6e",
    outline: "none", boxSizing: "border-box", transition: "all 0.15s ease"
  };

  return (
    <div style={{ minHeight: "100vh", backgroundColor: "#6c63ff", display: "flex", alignItems: "center", justifyContent: "center", fontFamily: "-apple-system, sans-serif", padding: "24px", boxSizing: "border-box" }}>
      <div style={{ width: "100%", maxWidth: "1000px", minHeight: "600px", background: "#f4f5fa", borderRadius: "24px", display: "grid", gridTemplateColumns: "1.1fr 0.9fr", overflow: "hidden", boxShadow: "0 20px 50px rgba(0,0,0,0.15)" }}>

        <div style={{ background: "#ffffff", display: "flex", flexDirection: "column", justifyContent: "space-between", padding: "48px 56px" }}>
          <div>
            <button type="button" style={{ background: "#f8f8fc", border: "1px solid #ebebf5", borderRadius: "50%", width: "36px", height: "36px", display: "flex", alignItems: "center", justifyContent: "center", color: "#4b4b75", cursor: "pointer" }} onClick={() => router.push("/login")}>
              <ArrowLeftIcon style={{ width: "16px", height: "16px" }} />
            </button>
          </div>

          <div style={{ width: "100%", maxWidth: "340px", margin: "auto 0" }}>
            <h1 style={{ fontSize: "24px", fontWeight: "600", color: "#2d2d6e", marginBottom: "8px" }}>Create Account</h1>
            <p style={{ fontSize: "14px", color: "#8888a0", marginBottom: "24px" }}>Get started with your merchant account.</p>

            <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
              {[
                { icon: UserIcon, name: "name", type: "text", placeholder: "Enter Name" },
                { icon: EnvelopeIcon, name: "username", type: "email", placeholder: "Enter Email" }
              ].map((f) => (
                <div key={f.name} style={{ position: "relative" }}>
                  <f.icon style={{ position: "absolute", left: "14px", top: "50%", transform: "translateY(-50%)", width: "18px", height: "18px", color: "#b0b0c8", pointerEvents: "none" }} />
                  <input type={f.type} name={f.name} style={inputStyle} value={user[f.name]} onChange={handleChange} placeholder={f.placeholder} required />
                </div>
              ))}

              <div style={{ position: "relative" }}>
                <ShieldCheckIcon style={{ position: "absolute", left: "14px", top: "50%", transform: "translateY(-50%)", width: "18px", height: "18px", color: "#b0b0c8", pointerEvents: "none" }} />
                <select name="roles" style={{ ...inputStyle, appearance: "none" }} value={user.roles} onChange={handleChange} required>
                  <option value="">Select Role</option>
                  {roles.map((r) => <option key={r.id} value={r.identifier}>{r.identifier}</option>)}
                </select>
              </div>

              <div style={{ position: "relative" }}>
                <PhoneIcon style={{ position: "absolute", left: "14px", top: "50%", transform: "translateY(-50%)", width: "18px", height: "18px", color: "#b0b0c8", pointerEvents: "none" }} />
                <input type="text" inputMode="numeric" name="phoneNo" style={inputStyle} value={user.phoneNo} onChange={handleChange} placeholder="Enter Phone Number (10 digits)" required />
              </div>

              <div style={{ position: "relative" }}>
                <LockClosedIcon style={{ position: "absolute", left: "14px", top: "50%", transform: "translateY(-50%)", width: "18px", height: "18px", color: "#b0b0c8", pointerEvents: "none" }} />
                <input type="password" name="password" style={inputStyle} value={user.password} onChange={handleChange} placeholder="Password (min 6 chars, letters & numbers)" required />
              </div>

              {success && <div style={{ backgroundColor: "#f2fdf5", border: "1px solid #d3f9df", color: "#10b981", fontSize: "13px", borderRadius: "8px", padding: "10px 12px", textAlign: "center" }}>{success}</div>}
              {error && <div style={{ backgroundColor: "#fff2f2", border: "1px solid #ffd6d6", color: "#e55555", fontSize: "13px", borderRadius: "8px", padding: "10px 12px", textAlign: "center" }}>{error}</div>}

              <button type="submit" style={{ width: "100%", height: "46px", background: "#6c63ff", border: "none", borderRadius: "10px", color: "#ffffff", fontSize: "14px", fontWeight: "500", cursor: "pointer" }}>Create Account</button>
            </form>

            <p style={{ fontSize: "13px", color: "#8888a0", marginTop: "20px" }}>Already have an account? <button type="button" style={{ color: "#6c63ff", background: "none", border: "none", padding: 0, fontWeight: 600, cursor: "pointer" }} onClick={() => router.push("/login")}>Sign In</button></p>
          </div>

          <div style={{ fontSize: "12px", color: "#b0b0c8" }}>© POS 2026</div>
        </div>

        <div style={{ background: "#f4f5fa", display: "flex", flexDirection: "column", justifyContent: "space-between", padding: "48px" }}>
          <div style={{ display: "flex", justifyContent: "flex-end" }}>
            <button type="button" onClick={() => router.push("/login")} style={{ background: "#ffffff", border: "none", borderRadius: "20px", padding: "8px 20px", fontSize: "13px", fontWeight: "500", color: "#4b4b75", cursor: "pointer", boxShadow: "0 2px 10px rgba(0,0,0,0.04)" }}>
              Sign In
            </button>
          </div>

          <div style={{ display: "flex", gap: "20px", fontSize: "12px" }}>
            <a href="/terms" style={{ color: "#8888a0", textDecoration: "none" }}>Terms & Condition</a>
            <a href="/privacy" style={{ color: "#8888a0", textDecoration: "none" }}>Privacy Policy</a>
            <a href="/help" style={{ color: "#8888a0", textDecoration: "none" }}>Help</a>
          </div>
        </div>

      </div>
    </div>
  );
};

export default Register;