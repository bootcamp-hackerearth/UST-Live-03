"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function LoginPage() {
  const router = useRouter();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const [error, setError] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);
      setError("");

      const res = await fetch("/api/authenticate", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ username, password })
      });

      const data = await res.json();

      if (!res.ok || !data.token) {
        throw new Error("Invalid username or password");
      }

      const cookieRes = await fetch("/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          token: data.token
        })
      });

      if (!cookieRes.ok) {
        throw new Error("Failed to set cookie");
      }

      localStorage.setItem("token", data.token);
      localStorage.setItem("username", username);

      router.push("/dashboard");

    } catch (err) {
      console.error(err);
      setError(err.message || "Login failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 flex">

      <div className="hidden lg:flex w-1/2 bg-slate-900 text-white p-16 flex-col justify-center">
        <div className="max-w-lg">

          <div className="mb-8">
            <div className="w-16 h-16 rounded-2xl bg-white text-slate-900 flex items-center justify-center text-2xl font-bold">
              P
            </div>
          </div>

          <h1 className="text-5xl font-bold leading-tight">
            Smart POS <span className="block text-slate-300">Management System</span>
          </h1>

          <div className="mt-10 space-y-4">
            <div className="flex items-center gap-3">
              <div className="w-2 h-2 rounded-full bg-white"></div>
              <span className="text-slate-300">
                Product Management
              </span>
            </div>

            <div className="flex items-center gap-3">
              <div className="w-2 h-2 rounded-full bg-white"></div>
              <span className="text-slate-300">
                Inventory Tracking
              </span>
            </div>

            <div className="flex items-center gap-3">
              <div className="w-2 h-2 rounded-full bg-white"></div>
              <span className="text-slate-300">
                User & Role Management
              </span>
            </div>

            <div className="flex items-center gap-3">
              <div className="w-2 h-2 rounded-full bg-white"></div>
              <span className="text-slate-300">
                Billing & Reports
              </span>
            </div>
          </div>

        </div>
      </div>

      <div className="w-full lg:w-1/2 flex items-center justify-center p-6">

        <div className="w-full max-w-md bg-white border border-slate-200 rounded-2xl shadow-xl p-8">

          <div className="mb-8 text-center">
            <h2 className="text-3xl font-bold text-slate-800">
              Welcome Back
            </h2>

            <p className="mt-2 text-sm text-slate-500">
              Sign in to access your dashboard
            </p>
          </div>

          <form onSubmit={handleLogin} className="space-y-5">

            <div>
              <label htmlFor="username" className="block text-sm font-medium text-slate-600 mb-2">
                Username
              </label>

              <input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter username"
                className="w-full px-4 py-3 border border-slate-300 rounded-xl outline-none focus:ring-2 focus:ring-slate-900 focus:border-slate-900 transition"
                required
              />
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-slate-600 mb-2">
                Password
              </label>

              <div className="relative">
                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Enter password"
                  className="w-full px-4 py-3 pr-20 border border-slate-300 rounded-xl outline-none focus:ring-2 focus:ring-slate-900 focus:border-slate-900 transition"
                  required
                />

                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-sm text-slate-500 hover:text-slate-900"
                >
                  {showPassword ? "Hide" : "Show"}
                </button>
              </div>
            </div>

            {error && (
              <div className="text-red-500 text-sm text-center">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 rounded-xl bg-slate-900 text-white font-medium hover:bg-black transition disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? "Signing In..." : "Login"}
            </button>

            <div className="text-center pt-2">
              <span className="text-sm text-slate-500">
                New user?
              </span>

              <button
                type="button"
                onClick={() => router.push("/register")}
                className="ml-2 text-sm font-medium text-slate-900 hover:underline"
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