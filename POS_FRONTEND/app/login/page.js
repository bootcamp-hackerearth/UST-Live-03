"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import api from "../services/api";
import { Eye, EyeOff } from "lucide-react";

export default function Login() {
  const router = useRouter();

  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    if (error) setError("");

    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);
      setError("");

      const res = await api.post("/api/authenticate", formData);
      const token = res.data?.token || res.data?.data?.token;

      if (!token) throw new Error("Invalid response");

      localStorage.setItem("token", token);
      localStorage.setItem("username", formData.username);

      router.replace("/home");
    } catch (err) {
      setError(
        err.response?.data?.message ||
        err.message ||
        "Invalid username or password"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex">

      
      <div className="hidden md:flex w-1/2 bg-gradient-to-br from-[#020617] via-[#020c2f] to-[#0a1f66] text-white flex-col justify-center px-16">
        <h1 className="text-4xl font-bold">
          Welcome Back 
        </h1>

        <p className="mt-4 text-blue-200 text-lg">
          Manage your system efficiently and securely.
        </p>

        <div className="mt-10 space-y-3 text-blue-300 text-sm">
          <p>✔ Secure authentication</p>
          <p>✔ Fast performance</p>
          <p>✔ Enterprise ready</p>
        </div>
      </div>

    
      <div className="w-full md:w-1/2 flex items-center justify-center bg-white px-6">

        <div className="w-full max-w-md bg-gradient-to-br from-[#0a1f66] via-[#0f2a8a] to-[#1e3a8a] border border-white/10 shadow-2xl rounded-2xl text-white">

          <div className="text-center p-6 border-b border-white/10">
            <h2 className="text-2xl font-bold">
              Sign In
            </h2>
            <p className="text-blue-200 text-sm mt-1">
              Enter your credentials
            </p>
          </div>

          <form onSubmit={handleSubmit} className="p-6 space-y-5">

            <Input
              id="username"
              label="Email Address"
              name="username"
              type="email"
              placeholder="name@example.com"
              value={formData.username}
              onChange={handleChange}
            />

            <div>
              <label
                htmlFor="password"
                className="text-xs text-blue-200 uppercase"
              >
                Password
              </label>

              <div className="relative mt-1">
                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  required
                  placeholder="••••••••"
                  className="w-full px-3 py-2 pr-10 rounded-lg bg-black/20 border border-white/20 text-white focus:outline-none focus:ring-2 focus:ring-blue-300 placeholder:text-blue-300"
                />

                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-2 top-1/2 -translate-y-1/2 text-blue-200"
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            {error && (
              <div className="bg-red-400/20 text-red-200 text-sm p-2 rounded">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full py-2.5 rounded-lg bg-white text-blue-800 font-semibold hover:bg-blue-100 transition"
            >
              {loading ? "Signing in..." : "Login"}
            </button>

            <div className="flex justify-between text-xs text-blue-200">
              <button
                type="button"
                className="hover:underline"
                onClick={() => router.push("/forgot-password")}
              >
                Forgot Password?
              </button>

              <button
                type="button"
                className="hover:underline"
                onClick={() => router.push("/register")}
              >
                Create Account
              </button>
            </div>

          </form>
        </div>
      </div>
    </div>
  );
}

function Input({ label, id, ...props }) {
  return (
    <div>
      <label
        htmlFor={id}
        className="text-xs text-blue-200 uppercase"
      >
        {label}
      </label>

      <input
        id={id}
        {...props}
        required
        className="w-full mt-1 px-3 py-2 rounded-lg bg-black/20 border border-white/20 text-white focus:outline-none focus:ring-2 focus:ring-blue-300 placeholder:text-blue-300"
      />
    </div>
  );
}

Input.propTypes = {
  label: PropTypes.string.isRequired,
  id: PropTypes.string.isRequired,
  name: PropTypes.string,
  type: PropTypes.string,
  value: PropTypes.any,
  onChange: PropTypes.func,
};