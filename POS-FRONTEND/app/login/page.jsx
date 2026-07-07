"use client";

import React, { useState } from "react";
import axios from "axios";
import { useRouter } from "next/navigation";

function Login() {
  const [credentials, setCredentials] = useState({ username: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const router = useRouter();

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
    setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const response = await axios.post(/api/authenticate",
        credentials
      );
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("username", credentials.username);
      router.push("/home");
    } catch {
      setError("Invalid email or password. Please try again.");
    } finally {
      setLoading(false);
    }
  };const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const response = await axios.post("api/authenticate",
        credentials
      );
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("username", credentials.username);
      router.push("/home");
    } catch {
      setError("Invalid email or password. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-5">
      <div className="w-full max-w-2xl bg-white rounded-2xl shadow-md overflow-hidden">

        <div className="flex items-center justify-between px-8 pt-7 pb-5 border-b border-gray-100">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-lg bg-red-600 flex items-center justify-center text-white text-sm font-bold">
              N
            </div>
            <span className="text-lg font-bold text-gray-900 tracking-tight">POS</span>
          </div>
          <span className="text-sm text-gray-400 font-medium">Staff Login</span>
        </div>

        <div className="px-8 py-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-1">Welcome Back</h2>
          <p className="text-sm text-gray-400 mb-7">Sign in to your POS account</p>

          {error && (
            <div className="mb-5 px-4 py-3 bg-red-50 border border-red-200 text-red-600 text-sm rounded-lg">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="grid grid-cols-2 gap-5 mb-5">
              <div>
                <label htmlFor="username" className="block text-sm font-semibold text-gray-700 mb-1.5">
                  Email <span className="text-red-500">*</span>
                </label>
                <input
                  id="username"
                  name="username"
                  type="email"
                  value={credentials.username}
                  onChange={handleChange}
                  placeholder="example@gmail.com"
                  required
                  className="w-full h-11 border border-gray-200 rounded-lg px-3.5 text-sm text-gray-900 placeholder-gray-300
                             focus:outline-none focus:border-red-400 focus:ring-2 focus:ring-red-100 transition"
                />
              </div>

              <div>
                <label htmlFor="password" className="block text-sm font-semibold text-gray-700 mb-1.5">
                  Password <span className="text-red-500">*</span>
                </label>
                <div className="relative">
                  <input
                    name="password"
                    type={showPassword ? "text" : "password"}
                    value={credentials.password}
                    onChange={handleChange}
                    placeholder="Enter your password"
                    required
                    className="w-full h-11 border border-gray-200 rounded-lg px-3.5 pr-10 text-sm text-gray-900 placeholder-gray-300
                               focus:outline-none focus:border-red-400 focus:ring-2 focus:ring-red-100 transition"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition"
                    tabIndex={-1}
                  >
                    {showPassword ? (
                      <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24"
                        fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
                        <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
                        <line x1="1" y1="1" x2="23" y2="23"/>
                      </svg>
                    ) : (
                      <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24"
                        fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                        <circle cx="12" cy="12" r="3"/>
                      </svg>
                    )}
                  </button>
                </div>
              </div>
            </div>

            <div className="text-right mb-7">
              <button
                type="button"
                className="text-sm text-red-500 font-medium hover:underline bg-transparent border-none p-0 cursor-pointer"
              >
                Forgot password?
              </button>
            </div>

            <div className="flex items-center justify-between">
              <p className="text-sm text-gray-500">
                New user?{" "}
                <button
                  type="button"
                  onClick={() => router.push("/register")}
                  className="text-red-500 font-semibold hover:underline bg-transparent border-none p-0 cursor-pointer"
                >
                  Create account
                </button>
              </p>

              <button
                type="submit"
                disabled={loading}
                className="flex items-center gap-2 bg-red-600 hover:bg-red-700 disabled:opacity-60
                           text-white text-sm font-semibold px-6 py-2.5 rounded-lg transition cursor-pointer disabled:cursor-not-allowed"
              >
                {loading ? "Signing in..." : "Login →"}
              </button>
            </div>
          </form>
        </div>

      </div>
    </div>
  );
}

export default Login;
