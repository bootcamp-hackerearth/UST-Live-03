"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axiosInstance from "../api/axiosInstance";
import axios from "axios";

export default function Register() {
  const router = useRouter();

  const [username, setUsername] = useState("");
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [phoneNo, setPhoneNo] = useState("");
  const [roles, setRoles] = useState([]);
  const [selectedRoles, setSelectedRoles] = useState([]);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/role/findAllActive")
      .then((res) => setRoles(Array.isArray(res.data) ? res.data : []))
      .catch(() => {
        setRoles([]);
        setError("Could not load roles. Make sure the backend server is running.");
      });
  }, []);

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);
    try {
      const response = await axiosInstance.post("/user/add", {
        username, name, password, phoneNo, roles: selectedRoles,
      });
      const data = response.data;
      if (data.success) {
        setSuccess("Registration successful! Redirecting to login...");
        setTimeout(() => router.push("/Login"), 1500);
      } else {
        setError(data.message || "Registration failed. Please try again.");
      }
    } catch (err) {
      setError(err.response?.data?.message || err.message || "Registration failed.");
    } finally {
      setLoading(false);
    }
  };

  const inputClass = "border border-slate-300 rounded-xl px-4 py-3 text-sm text-slate-900 bg-slate-50 outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-100 focus:bg-white transition";

  return (
    <div className="min-h-screen flex bg-slate-900">

      <div className="hidden lg:flex lg:w-1/2 flex-col justify-between p-12 bg-gradient-to-br from-blue-700 to-blue-900">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-lg bg-white flex items-center justify-center">
            <span className="text-blue-700 font-black text-sm">POS</span>
          </div>
          <span className="text-white font-bold text-xl">RetailPOS</span>
        </div>

        <div>
          <h1 className="text-4xl font-bold text-white leading-tight mb-4">
            Create your<br />POS account.
          </h1>
          <p className="text-blue-200 text-sm leading-relaxed">
            Register to get access to inventory management, billing, and all POS features.
          </p>
        </div>

        <div className="flex gap-6">
          {["Products", "Inventory", "Reports", "Users"].map((item) => (
            <div key={item} className="text-center">
              <div className="w-10 h-10 rounded-xl bg-white/10 flex items-center justify-center mb-1">
                <span className="text-white text-xs font-bold">{item[0]}</span>
              </div>
              <span className="text-blue-200 text-xs">{item}</span>
            </div>
          ))}
        </div>
      </div>

      <div className="flex-1 flex items-center justify-center px-6 py-12 bg-white overflow-y-auto">
        <div className="w-full max-w-md">

          <div className="flex lg:hidden items-center gap-2 mb-8">
            <div className="w-8 h-8 rounded-lg bg-blue-700 flex items-center justify-center">
              <span className="text-white font-black text-xs">POS</span>
            </div>
            <span className="text-slate-800 font-bold text-lg">RetailPOS</span>
          </div>

          <h2 className="text-2xl font-bold text-slate-900 mb-1">Create Account</h2>
          <p className="text-slate-500 text-sm mb-8">Fill in the details to register a new POS user.</p>

          {error && (
            <div className="mb-5 rounded-xl bg-rose-50 border border-rose-200 px-4 py-3 text-sm text-rose-600 font-medium">
              {error}
            </div>
          )}
          {success && (
            <div className="mb-5 rounded-xl bg-emerald-50 border border-emerald-200 px-4 py-3 text-sm text-emerald-700 font-medium">
              {success}
            </div>
          )}

          <form onSubmit={handleRegister} className="flex flex-col gap-4">

            <div className="grid grid-cols-2 gap-4">
              <div className="flex flex-col gap-1">
                <label htmlFor="username" className="text-sm font-semibold text-slate-600">Username</label>
                <input
                  id="username"
                  type="email"
                  placeholder="Enter username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                  className={inputClass}
                />
              </div>

              <div className="flex flex-col gap-1">
                <label htmlFor="name" className="text-sm font-semibold text-slate-600">Full Name</label>
                <input
                  id="name"
                  type="text"
                  placeholder="Enter full name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                  className={inputClass}
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="flex flex-col gap-1">
                <label htmlFor="password" className="text-sm font-semibold text-slate-600">Password</label>
                <input
                  id="password"
                  type="password"
                  minLength={6}
                  placeholder="Enter password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  className={inputClass}
                />
              </div>

              <div className="flex flex-col gap-1">
                <label htmlFor="phoneNo" className="text-sm font-semibold text-slate-600">Phone Number</label>
                <input
                  id="phoneNo"
                  type="tel"
                  pattern="[0-9]{10}"
                  maxLength={10}
                  placeholder="Enter phone number"
                  value={phoneNo}
                  onKeyDown={(e) => {
                    if (!/\d/.test(e.key) && e.key !== "Backspace" && e.key !== "Delete" && e.key !== "Tab")
                      e.preventDefault();
                  }}
                  onChange={(e) => setPhoneNo(e.target.value)}
                  className={inputClass}
                />
              </div>
            </div>

            <div className="flex flex-col gap-1">
              <label htmlFor="roles" className="text-sm font-semibold text-slate-600">Roles</label>
              <select
                id="roles"
                multiple
                size={4}
                onChange={(e) => setSelectedRoles(Array.from(e.target.selectedOptions, (o) => o.value))}
                className="border border-slate-300 rounded-xl px-4 py-2 text-sm text-slate-900 bg-slate-50 outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-100 transition"
              >
                {roles.map((role) => (
                  <option key={role.identifier} value={role.identifier} className="py-2 text-slate-900">
                    {role.identifier}
                  </option>
                ))}
              </select>
              <p className="text-xs text-slate-400 mt-1">Hold Ctrl to select multiple roles</p>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full rounded-xl bg-blue-700 py-3 text-sm font-bold text-white hover:bg-blue-800 transition disabled:opacity-60 disabled:cursor-not-allowed mt-2"
            >
              {loading ? "Registering..." : "Create Account"}
            </button>

            <div className="relative flex items-center gap-3">
              <div className="flex-1 h-px bg-slate-200" />
              <span className="text-xs text-slate-400">or</span>
              <div className="flex-1 h-px bg-slate-200" />
            </div>

            <button
              type="button"
              onClick={() => router.push("/Login")}
              className="w-full rounded-xl border-2 border-slate-200 py-3 text-sm font-bold text-slate-700 hover:border-blue-400 hover:text-blue-700 transition"
            >
              Already have an account? Sign In
            </button>

          </form>

          <p className="text-center text-xs text-slate-400 mt-8">
            © {new Date().getFullYear()} RetailPOS. All rights reserved.
          </p>
        </div>
      </div>

    </div>
  );
}