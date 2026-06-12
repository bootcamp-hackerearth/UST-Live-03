"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import api from "../components/Axios";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [serverError, setServerError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const FIELD_REQUIRED_MSG = "This field is required.";
  const router = useRouter();

  function validate() {
    const errors = {};
    if (!username.trim()) errors.username = "Username is required.";
    if (!password.trim()) errors.password = FIELD_REQUIRED_MSG;
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  }

  async function handleLogin(e) {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    setServerError("");

    try {
      const response = await api.post("/authenticate", { username, password });

      if (response.data?.token) {
        localStorage.setItem("token", response.data.token);
        router.push("/home");
      } else {
        setServerError("Invalid username or password.");
      }
    } catch (err) {
      if (err.response) {
        setServerError("Invalid username or password.");
      } else {
        setServerError("Cannot reach server. Please check your connection.");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen bg-[#f5f5f0] flex items-center justify-center font-sans p-5">
      <div className="bg-white rounded-xl p-10 w-full max-w-[420px] shadow-[0_4px_24px_rgba(0,0,0,0.08)]">
        <div className="text-center mb-8">
          <div className="text-[28px] mb-2">🛒</div>
          <h1 className="text-2xl font-bold text-[#1a1a1a] m-0 mb-1">RetailPOS</h1>
          <p className="text-sm text-gray-500 m-0">Sign in to your account</p>
        </div>

        {serverError && (
          <div className="bg-[#fff5f5] border border-solid border-red-200 text-red-700 rounded-lg py-2.5 px-3.5 text-sm mb-4 text-center">
            {serverError}
          </div>
        )}

        <form onSubmit={handleLogin} noValidate>
          <div className="mb-4.5">
            <label className="block text-xs font-semibold text-gray-700 mb-1.5" htmlFor="username">
              Username
            </label>
            <input
              id="username"
              type="text"
              placeholder="Enter your username"
              value={username}
              className={`w-full px-3.5 py-2.5 border-[1.5px] border-solid rounded-lg text-sm outline-none transition-colors bg-[#fafaf8] ${
                fieldErrors.username ? "border-red-500 focus:border-red-600" : "border-gray-300 focus:border-brand"
              }`}
              onChange={(e) => {
                setUsername(e.target.value);
                if (fieldErrors.username) {
                  setFieldErrors((prev) => ({ ...prev, username: "" }));
                }
              }}
            />
            {fieldErrors.username && (
              <p className="text-xs text-red-500 mt-1">{fieldErrors.username}</p>
            )}
          </div>

          <div className="mb-4.5">
            <label className="block text-xs font-semibold text-gray-700 mb-1.5" htmlFor="password">
              Password
            </label>
            <div className="relative">
              <input
                id="password"
                type={showPassword ? "text" : "password"}
                placeholder="Enter your password"
                value={password}
                className={`w-full px-3.5 py-2.5 border-[1.5px] border-solid rounded-lg text-sm outline-none transition-colors bg-[#fafaf8] pr-10 ${fieldErrors.password ? "border-red-500 focus:border-red-600" : "border-gray-300 focus:border-brand"
                  }`}
                onChange={(e) => {
                  setPassword(e.target.value);
                  if (fieldErrors.password) {
                    setFieldErrors((prev) => ({ ...prev, password: "" }));
                  }
                }}
              />
              <button
                type="button"
                onClick={() => setShowPassword((prev) => !prev)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 bg-transparent border-none cursor-pointer p-0"
              >
              </button>
            </div>
            {fieldErrors.password && (
              <p className="text-xs text-red-500 mt-1">{fieldErrors.password}</p>
            )}
          </div>

          <button
            type="submit"
            disabled={loading}
            className={`w-full p-3 text-white border-none rounded-lg text-sm font-semibold mt-2 transition-colors ${
              loading ? "bg-gray-400 cursor-not-allowed" : "bg-brand hover:bg-emerald-800 cursor-pointer"
            }`}
          >
            {loading ? "Signing in…" : "Sign In"}
          </button>
        </form>

        <div className="text-center mt-5 text-sm text-gray-500">
          Don't have an account?
          <Link href="/register" className="text-brand font-semibold no-underline ml-1 hover:underline">
            Create one
          </Link>
        </div>
      </div>
    </div>
  );
}