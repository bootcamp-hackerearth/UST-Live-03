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
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
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
    <div className="min-h-screen flex items-center justify-center bg-gray-100 px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-lg border border-gray-200">

        <div className="text-center p-6 border-b">
          <h2 className="text-2xl font-bold text-gray-900">
            Welcome Back
          </h2>
          <p className="text-gray-500 text-sm mt-1">
            Login to continue
          </p>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">

          <Input
            label="Email Address"
            id="username"
            name="username"
            type="email"
            placeholder="name@example.com"
            value={formData.username}
            onChange={handleChange}
          />

          <div className="space-y-1">
            <label
              htmlFor="password"
              className="text-xs font-semibold uppercase text-gray-600"
            >
              Password
            </label>

            <div className="relative">
              <input
                id="password"
                type={showPassword ? "text" : "password"}
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="••••••••"
                autoComplete="off"
                required
                className="w-full px-3 py-2 pr-10 border border-gray-300 rounded-lg text-sm text-gray-800 bg-white focus:outline-none focus:ring-2 focus:ring-black"
              />

              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-500 hover:text-black"
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>

          {error && (
            <div className="bg-red-100 text-red-600 text-sm p-2 rounded-lg">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-black text-white py-2.5 rounded-lg font-medium hover:bg-gray-800 transition disabled:opacity-50"
          >
            {loading ? "Logging in..." : "Login"}
          </button>

          <div className="flex items-center gap-2">
            <div className="flex-1 h-px bg-gray-200"></div>
            <span className="text-xs text-gray-400">OR</span>
            <div className="flex-1 h-px bg-gray-200"></div>
          </div>

          <button
            type="button"
            onClick={() => router.push("/register")}
            className="w-full border py-2.5 rounded-lg text-sm text-gray-700 hover:bg-gray-50 transition"
          >
            Create Account
          </button>

        </form>
      </div>
    </div>
  );
}

function Input({ label, id, ...props }) {
  return (
    <div className="space-y-1">
      <label
        htmlFor={id}
        className="text-xs font-semibold uppercase text-gray-600"
      >
        {label}
      </label>

      <input
        id={id}
        {...props}
        required
        className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-800 bg-white focus:outline-none focus:ring-2 focus:ring-black"
      />
    </div>
  );
}

Input.propTypes = {
  label: PropTypes.string.isRequired,
  id: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  type: PropTypes.string,
  value: PropTypes.any,
  onChange: PropTypes.func,
};
