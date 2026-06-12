import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

function Login() {
  const [credentials, setCredentials] = useState({ username: "", password: "" });
  const [error, setError]             = useState("");
  const [loading, setLoading]         = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
    setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const response = await axios.post(
        "http://localhost:8080/api/authenticate",
        credentials
      );
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("username", credentials.username);
      navigate("/home");
    } catch {
      setError("Invalid email or password. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-5">
      <div className="flex w-full max-w-2xl rounded-2xl overflow-hidden shadow-xl">

        {/* ── Left sidebar (matches register page) ── */}
        <div className="w-48 flex-shrink-0 bg-[#1e2a3b] p-7 flex flex-col gap-5">

          {/* Logo */}
          <div className="flex items-center gap-2.5">
            <div className="w-10 h-10 rounded-xl bg-indigo-500 flex items-center justify-center text-white text-xs font-bold">
              POS
            </div>
            <div>
              <p className="text-white text-sm font-semibold leading-tight">POS System</p>
              <p className="text-slate-400 text-[11px]">Staff Portal</p>
            </div>
          </div>

          {/* Steps */}
          <div className="flex flex-col gap-4 mt-1">
            {/* Step 1 — active */}
            <div className="flex items-center gap-2.5">
              <div className="w-6 h-6 rounded-full bg-indigo-500 flex items-center justify-center text-white text-[11px] font-semibold flex-shrink-0">
                1
              </div>
              <div>
                <p className="text-white text-[12px] font-medium">Account login</p>
                <p className="text-slate-500 text-[10px]">Email &amp; password</p>
              </div>
            </div>

            {/* Step 2 — inactive */}
            <div className="flex items-center gap-2.5 opacity-40">
              <div className="w-6 h-6 rounded-full bg-[#2d3f55] flex items-center justify-center text-slate-400 text-[11px] font-semibold flex-shrink-0">
                2
              </div>
              <div>
                <p className="text-slate-400 text-[12px] font-medium">Access &amp; role</p>
                <p className="text-slate-500 text-[10px]">Assign permissions</p>
              </div>
            </div>

            {/* Step 3 — inactive */}
            <div className="flex items-center gap-2.5 opacity-40">
              <div className="w-6 h-6 rounded-full bg-[#2d3f55] flex items-center justify-center text-slate-400 text-[11px] font-semibold flex-shrink-0">
                3
              </div>
              <div>
                <p className="text-slate-400 text-[12px] font-medium">Dashboard</p>
                <p className="text-slate-500 text-[10px]">Your workspace</p>
              </div>
            </div>
          </div>

          <p className="mt-auto text-[10px] text-slate-600 leading-relaxed">
            Need help? Contact your<br />system administrator.
          </p>
        </div>

        {/* ── Right panel ── */}
        <div className="flex-1 bg-white px-8 py-9">
          <h2 className="text-xl font-bold text-slate-900 text-center mb-1">
            Welcome back
          </h2>
          <p className="text-sm text-slate-500 text-center mb-7">
            Login to your POS account
          </p>

          {/* Error */}
          {error && (
            <div className="mb-5 px-4 py-3 bg-red-50 border border-red-200 text-red-600 text-sm rounded-lg">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            {/* Email & Password row */}
            <div className="grid grid-cols-2 gap-4 mb-4">
              <div>
                <label className="block text-[11px] font-semibold text-gray-500 uppercase tracking-wide mb-1.5">
                  Email
                </label>
                <input
                  name="username"
                  type="email"
                  value={credentials.username}
                  onChange={handleChange}
                  placeholder="you@example.com"
                  required
                  className="w-full h-10 border border-slate-200 rounded-lg px-3 text-sm text-slate-900 placeholder-slate-300
                             focus:outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition"
                />
              </div>

              <div>
                <label className="block text-[11px] font-semibold text-gray-500 uppercase tracking-wide mb-1.5">
                  Password
                </label>
                <input
                  name="password"
                  type="password"
                  value={credentials.password}
                  onChange={handleChange}
                  placeholder="Enter your password"
                  required
                  className="w-full h-10 border border-slate-200 rounded-lg px-3 text-sm text-slate-900 placeholder-slate-300
                             focus:outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition"
                />
              </div>
            </div>

            {/* Forgot password */}
            <div className="text-right mb-5">
              <a href="#" className="text-[12px] text-indigo-500 font-medium hover:underline">
                Forgot password?
              </a>
            </div>

            <hr className="border-slate-100 mb-5" />

            {/* Footer row */}
            <div className="flex items-center justify-between">
              <p className="text-[12px] text-slate-500">
                New user?{" "}
                <button
                  type="button"
                  onClick={() => navigate("/register")}
                  className="text-indigo-500 font-semibold hover:underline bg-transparent border-none p-0 cursor-pointer"
                >
                  Create account
                </button>
              </p>

              <button
                type="submit"
                disabled={loading}
                className="flex items-center gap-1.5 bg-indigo-500 hover:bg-indigo-600 disabled:opacity-60
                           text-white text-sm font-semibold px-5 py-2.5 rounded-lg transition cursor-pointer disabled:cursor-not-allowed"
              >
                {loading ? "Logging in..." : "Login →"}
              </button>
            </div>
          </form>
        </div>

      </div>
    </div>
  );
}

export default Login;