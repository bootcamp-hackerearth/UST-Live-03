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
        localStorage.setItem("username", data.username);
        router.push("/Dashboard");
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
    <div className="min-h-screen relative flex items-center justify-center px-4 py-12 bg-slate-950 overflow-hidden font-sans">
      
      <div className="absolute top-[-10%] left-[-10%] w-[50%] h-[50%] rounded-full bg-violet-600/20 blur-[120px] pointer-events-none" />
      <div className="absolute bottom-[-10%] right-[-10%] w-[50%] h-[50%] rounded-full bg-emerald-500/10 blur-[120px] pointer-events-none" />

      <div className="w-full max-w-md z-10">
        
        <div className="bg-slate-900/60 backdrop-blur-xl border border-slate-800 rounded-3xl p-8 md:p-10 shadow-2xl shadow-black/40">
          
          <div className="flex flex-col items-center text-center mb-8">
            <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-violet-600 to-indigo-600 flex items-center justify-center shadow-lg shadow-indigo-500/20 mb-4">
              <span className="text-white font-black text-sm tracking-wider">POS</span>
            </div>
            <h1 className="text-2xl font-extrabold text-white tracking-tight">RetailPOS</h1>
            <p className="text-slate-400 text-sm mt-1.5">Welcome back! Sign in to manage your ecosystem.</p>
          </div>

          {error && (
            <div className="mb-6 rounded-xl bg-rose-500/10 border border-rose-500/20 px-4 py-3.5 text-xs text-rose-400 font-medium flex items-center gap-2">
              <span className="w-1.5 h-1.5 rounded-full bg-rose-400 shrink-0" />
              {error}
            </div>
          )}

          <div className="space-y-5">
            <div className="space-y-1.5">
              <label htmlFor="username" className="text-xs font-medium text-slate-300 tracking-wide uppercase">
                Username / Email
              </label>
              <input
                id="username"
                type="text"
                placeholder="alex@retailpos.com"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                onKeyDown={handleKeyDown}
                className="w-full border border-slate-800 rounded-xl px-4 py-3 text-sm text-white bg-slate-950/50 outline-none focus:border-violet-500 focus:ring-1 focus:ring-violet-500/30 transition duration-200 placeholder:text-slate-600"
              />
            </div>

            <div className="space-y-1.5">
              <div className="flex justify-between items-center">
                <label htmlFor="password" className="text-xs font-medium text-slate-300 tracking-wide uppercase">
                  Password
                </label>
              </div>
              <input
                id="password"
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                onKeyDown={handleKeyDown}
                className="w-full border border-slate-800 rounded-xl px-4 py-3 text-sm text-white bg-slate-950/50 outline-none focus:border-violet-500 focus:ring-1 focus:ring-violet-500/30 transition duration-200 placeholder:text-slate-600"
              />
            </div>

            <button
              onClick={handleLogin}
              disabled={loading}
              className="w-full rounded-xl bg-gradient-to-r from-violet-600 to-indigo-600 py-3.5 text-sm font-semibold text-white hover:from-violet-500 hover:to-indigo-500 active:scale-[0.99] transition duration-150 disabled:opacity-40 disabled:cursor-not-allowed shadow-lg shadow-indigo-600/10 mt-3"
            >
              {loading ? (
                <div className="flex items-center justify-center gap-2">
                  <svg className="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                  </svg>
                  <span>Authenticating...</span>
                </div>
              ) : (
                "Login"
              )}
            </button>

            <div className="relative flex items-center justify-center py-2">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-slate-800/60" />
              </div>
              <span className="relative px-3 bg-[#0b1224] text-[11px] uppercase tracking-widest text-slate-500">
                New to the platform?
              </span>
            </div>

            <button
              onClick={() => router.push("/Register")}
              className="w-full rounded-xl border border-slate-800 bg-slate-950/20 py-3 text-sm font-medium text-slate-300 hover:bg-slate-800/40 hover:text-white transition duration-200"
            >
              New User? Register Here
            </button>
          </div>
        </div>

        <p className="text-center text-[11px] text-slate-500 mt-6 tracking-wide">
          &copy; {new Date().getFullYear()} RetailPOS Ecosystem. Encrypted End-to-End.
        </p>
      </div>
    </div>
  );
}