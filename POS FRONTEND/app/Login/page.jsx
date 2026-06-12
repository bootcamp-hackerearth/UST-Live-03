"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import axiosInstance from "../api/axiosInstance";

export default function Login() {
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogin = async () => {
    setError("");
    setLoading(true);
    try {
      const response = await axiosInstance.post("/authenticate", { username, password });
      const data = response.data;

      if (data.token && data.token !== "Error") {
        localStorage.setItem("token", data.token);
        localStorage.setItem("username", username);
        router.push("/");
      } else {
        setError(data.message || "Invalid credentials. Please try again.");
      }
    } catch (error) {
      setError(error.response?.data?.message || "Login failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") handleLogin();
  };

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
            Manage your store<br />smarter and faster.
          </h1>
          <p className="text-blue-200 text-sm leading-relaxed">
            Complete point-of-sale solution for inventory, billing, users, and reporting — all in one place.
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

    
      <div className="flex-1 flex items-center justify-center px-6 py-12 bg-white">
        <div className="w-full max-w-sm">

    
          <div className="flex lg:hidden items-center gap-2 mb-8">
            <div className="w-8 h-8 rounded-lg bg-blue-700 flex items-center justify-center">
              <span className="text-white font-black text-xs">POS</span>
            </div>
            <span className="text-slate-800 font-bold text-lg">RetailPOS</span>
          </div>

          <h2 className="text-2xl font-bold text-slate-900 mb-1">Sign in</h2>
          <p className="text-slate-500 text-sm mb-8">Enter your credentials to access the dashboard.</p>

          {error && (
            <div className="mb-5 rounded-xl bg-rose-50 border border-rose-200 px-4 py-3 text-sm text-rose-600 font-medium">
              {error}
            </div>
          )}

          <div className="flex flex-col gap-4">
            <div className="flex flex-col gap-1">
              <label htmlFor="username" className="text-sm font-semibold text-slate-600">Username</label>
              <input
                id="username"
                type="email"
                placeholder="Enter your username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                onKeyDown={handleKeyDown}
                className="border border-slate-300 rounded-xl px-4 py-3 text-sm text-slate-900 bg-slate-50 outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-100 focus:bg-white transition"
              />
            </div>

            <div className="flex flex-col gap-1">
              <label htmlFor="password" className="text-sm font-semibold text-slate-600">Password</label>
              <input
                id="password"
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                onKeyDown={handleKeyDown}
                className="border border-slate-300 rounded-xl px-4 py-3 text-sm text-slate-900 bg-slate-50 outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-100 focus:bg-white transition"
              />
            </div>

            <button
              onClick={handleLogin}
              disabled={loading}
              className="w-full rounded-xl bg-blue-700 py-3 text-sm font-bold text-white hover:bg-blue-800 transition disabled:opacity-60 disabled:cursor-not-allowed mt-2"
            >
              {loading ? "Signing in..." : "Sign In"}
            </button>

            <div className="relative flex items-center gap-3 my-1">
              <div className="flex-1 h-px bg-slate-200" />
              <span className="text-xs text-slate-400">or</span>
              <div className="flex-1 h-px bg-slate-200" />
            </div>

            <button
              onClick={() => router.push("/Register")}
              className="w-full rounded-xl border-2 border-slate-200 py-3 text-sm font-bold text-slate-700 hover:border-blue-400 hover:text-blue-700 transition"
            >
              Create New Account
            </button>
          </div>

          <p className="text-center text-xs text-slate-400 mt-8">
            © {new Date().getFullYear()} RetailPOS. All rights reserved.
          </p>
        </div>
      </div>

    </div>
  );
}