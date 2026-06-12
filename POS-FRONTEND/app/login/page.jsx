// app/login/page.jsx

"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Loader, KeyRound, CheckCircle2, Mail, Lock } from "lucide-react";
import api from "../api/axios";
import ustLogo from "@/assets/logo/UST-White-logo.png";

export default function LoginPage() {
  const router = useRouter();

  const [credentials, setCredentials] = useState({
    username: "",
    password: ""
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      router.push("/pos/home");
    }
  }, [router]);

  const handleChange = (e) => {
    setCredentials({
      ...credentials,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const response = await api.post("/authenticate", credentials);
      const data = response.data;

      if (data.success === false || data.token === "Error") {
        throw new Error(data.message || "Invalid security parameters");
      }

      localStorage.setItem("token", data.token);
      localStorage.setItem("username", data.username);
      localStorage.setItem("name", data.name);
      localStorage.setItem("roles", JSON.stringify(data.roles || []));

      localStorage.setItem("tokenLoginTime", Date.now().toString());

      router.push("/pos/home");

    } catch (err) {
      const errorMessage = err.response?.data?.message || err.message || "Authentication rejected";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex h-screen w-screen bg-[#FFFFFF] overflow-hidden">

      <div className="hidden lg:flex lg:w-1/2 bg-[#231F20] text-white p-16 flex-col justify-between relative border-r border-white/10">

        <div className="flex items-center gap-3">
          <img
            src={ustLogo.src}
            alt="UST Logo"
            className="w-10 h-10 object-contain"
          />
          <div>
            <span className="text-lg font-bold tracking-wider uppercase block">Retail POS</span>
            <span className="text-[11px] text-[#0097AC] font-medium tracking-widest uppercase">Developed By UST</span>
          </div>
        </div>

        <div className="max-w-md">
          <h1 className="text-5xl font-extrabold mb-6 leading-tight tracking-tight">
            Simplified <span className="text-[#0097AC]">Retail </span>Platform.
          </h1>
          <p className="text-sm text-white/50 mb-8 leading-relaxed">
            Access secure transaction ledgers, active inventory control matrices, and edge-terminal analytics pools.
          </p>

          <div className="space-y-4">
            {[
              "Real-time Edge Analytics",
              "Enterprise Token Storage",
              "Dynamic Stock Controls"
            ].map((text) => (
              <div key={text} className="flex items-center gap-3 text-sm font-medium text-white/80">
                <CheckCircle2 size={18} className="text-[#0097AC]" />
                {text}
              </div>
            ))}
          </div>
        </div>

        <div className="text-[10px] text-white/30 font-mono">
          v3.1.0-build || Sprint-3
        </div>
      </div>

      <div className="w-full lg:w-1/2 bg-slate-50 flex items-center justify-center p-6 sm:p-12">
        <div className="w-full max-w-md bg-white rounded-2xl shadow-xl shadow-[#231F20]/5 border border-[#231F20]/10 p-8 sm:p-10">

          <div className="flex items-center gap-2 mb-1">
            <KeyRound className="text-[#006E74]" size={22} />
            <h2 className="text-2xl font-bold text-[#231F20] tracking-tight">Sign In</h2>
          </div>
          <p className="text-[#231F20]/60 text-xs mb-6">Enter Your Credentials</p>

          {error && (
            <div className="mb-5 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl text-xs font-semibold">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">

            <div>
              <label
                htmlFor="login-email-input"
                className="block text-[10px] font-bold uppercase tracking-widest text-[#231F20]/60 mb-1.5 cursor-pointer"
              >
                Email Address
              </label>
              <div className="relative">
                <Mail size={16} className="absolute left-3 top-3 text-[#231F20]/30" />
                <input
                  id="login-email-input"
                  type="email"
                  name="username"
                  value={credentials.username}
                  onChange={handleChange}
                  placeholder="kushal@ust.com"
                  className="w-full pl-9 pr-4 py-2.5 bg-white text-[#231F20] border border-[#231F20]/20 rounded-xl text-sm placeholder-[#231F20]/30 focus:outline-none focus:border-[#006E74] focus:ring-4 focus:ring-[#006E74]/5 transition-all"
                  required
                />
              </div>
            </div>

            <div>
              <label
                htmlFor="login-password-input"
                className="block text-[10px] font-bold uppercase tracking-widest text-[#231F20]/60 mb-1.5 cursor-pointer"
              >
                Password
              </label>
              <div className="relative">
                <Lock size={16} className="absolute left-3 top-3 text-[#231F20]/30" />
                <input
                  id="login-password-input"
                  type="password"
                  name="password"
                  value={credentials.password}
                  onChange={handleChange}
                  placeholder="••••••••"
                  className="w-full pl-9 pr-4 py-2.5 bg-white text-[#231F20] border border-[#231F20]/20 rounded-xl text-sm placeholder-[#231F20]/30 focus:outline-none focus:border-[#006E74] focus:ring-4 focus:ring-[#006E74]/5 transition-all"
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-[#006E74] hover:bg-[#0097AC] disabled:bg-slate-200 disabled:text-[#231F20]/40 text-white font-bold py-2.5 rounded-xl transition-all shadow-md shadow-[#006E74]/10 flex items-center justify-center gap-2 mt-6 cursor-pointer"
            >
              {loading ? (
                <>
                  <Loader size={16} className="animate-spin text-white" />
                  <span className="text-sm font-semibold">Validating Credentials</span>
                </>
              ) : (
                "Login to Your Account"
              )}
            </button>

          </form>

          <div className="mt-6 text-center text-xs text-[#231F20]/60">
            New to the System?{" "}
            <Link href="/register" className="text-[#006E74] font-bold hover:text-[#0097AC] underline transition-colors">
              Create an account
            </Link>
          </div>

        </div>
      </div>
    </div>
  );
}