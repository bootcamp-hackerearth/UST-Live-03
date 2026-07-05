// app/login/page.jsx

"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
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
    setError("");
  };

  const validateInputForm = () => {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(credentials.username)) {
      return "Please enter a valid corporate email address structure.";
    }
    if (credentials.password.length < 6) {
      return "Password must meet the minimal length requirement (6 or more characters).";
    }
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formValidationError = validateInputForm();
    if (formValidationError) {
      setError(formValidationError);
      return;
    }

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
    <div className="flex flex-col lg:flex-row min-h-screen w-screen bg-[#181617] bg-gradient-to-br from-[#231F20] via-[#1a1819] to-[#0d2d31] lg:bg-none lg:bg-white overflow-y-auto lg:overflow-hidden font-sans">
      
      <div className="hidden lg:flex lg:w-1/2 bg-[#231F20] text-white p-16 flex-col justify-between relative border-r border-white/10">
        <div className="flex items-center gap-3">
          <img
            src={ustLogo.src}
            alt="UST Logo"
            className="w-10 h-10 object-contain"
          />
          <div className="text-left">
            <span className="text-lg font-bold tracking-wider uppercase block">Retail POS</span>
            <span className="text-[11px] text-[#0097AC] font-medium tracking-widest uppercase">Developed By UST</span>
          </div>
        </div>

        <div className="max-w-md text-left">
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

        <div className="text-[10px] text-white/30 font-mono text-left">
          v5.1.0-build || Sprint-5
        </div>
      </div>

      <div className="w-full lg:w-1/2 flex flex-col justify-center items-center p-5 sm:p-12 md:p-16 my-auto">
        
        <div className="flex lg:hidden items-center gap-3 mb-8 text-white w-full max-w-md px-2 text-left">
          <img
            src={ustLogo.src}
            alt="UST Logo"
            className="w-10 h-10 object-contain drop-shadow-[0_2px_8px_rgba(0,151,172,0.4)]"
          />
          <div>
            <span className="text-xl font-bold tracking-wider uppercase block leading-none mb-1">Retail POS</span>
            <span className="text-[10px] text-[#0097AC] font-bold tracking-widest uppercase block leading-none">Developed By UST</span>
          </div>
        </div>

        <div className="w-full max-w-md bg-[#231F20]/40 backdrop-blur-xl lg:bg-white rounded-2xl shadow-2xl shadow-black/40 lg:shadow-xl lg:shadow-[#231F20]/5 border border-white/10 lg:border-[#231F20]/10 p-8 sm:p-10 text-left transition-all">

          <div className="flex items-center gap-2 mb-1">
            <KeyRound className="text-[#0097AC] lg:text-[#006E74]" size={22} />
            <h2 className="text-2xl font-bold text-white lg:text-[#231F20] tracking-tight">Sign In</h2>
          </div>
          <p className="text-white/60 lg:text-[#231F20]/60 text-xs mb-6">Enter Your Credentials</p>

          {error && (
            <div className="mb-5 bg-red-500/10 border border-red-500/30 text-red-400 lg:bg-red-50 lg:border-red-200 lg:text-red-700 px-4 py-3 rounded-xl text-xs font-semibold animate-pulse">
              ⚠️ {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">

            <div>
              <label
                htmlFor="login-email-input"
                className="block text-[10px] font-bold uppercase tracking-widest text-white/50 lg:text-[#231F20]/60 mb-1.5 cursor-pointer"
              >
                Email Address
              </label>
              <div className="relative">
                <Mail size={16} className="absolute left-3 top-3 text-white/30 lg:text-[#231F20]/30" />
                <input
                  id="login-email-input"
                  type="email"
                  name="username"
                  value={credentials.username}
                  onChange={handleChange}
                  placeholder="kushal@ust.com"
                  className="w-full pl-9 pr-4 py-2.5 bg-white/5 lg:bg-white text-white lg:text-[#231F20] border border-white/10 lg:border-[#231F20]/20 rounded-xl text-sm placeholder-white/20 lg:placeholder-[#231F20]/30 focus:outline-none focus:border-[#0097AC] lg:focus:border-[#006E74] focus:ring-4 focus:ring-[#0097AC]/10 lg:focus:ring-[#006E74]/5 transition-all invalid:border-red-500/40"
                  required
                />
              </div>
            </div>

            <div>
              <label
                htmlFor="login-password-input"
                className="block text-[10px] font-bold uppercase tracking-widest text-white/50 lg:text-[#231F20]/60 mb-1.5 cursor-pointer"
              >
                Password
              </label>
              <div className="relative">
                <Lock size={16} className="absolute left-3 top-3 text-white/30 lg:text-[#231F20]/30" />
                <input
                  id="login-password-input"
                  type="password"
                  name="password"
                  minLength="6"
                  value={credentials.password}
                  onChange={handleChange}
                  placeholder="••••••••"
                  className="w-full pl-9 pr-4 py-2.5 bg-white/5 lg:bg-white text-white lg:text-[#231F20] border border-white/10 lg:border-[#231F20]/20 rounded-xl text-sm placeholder-white/20 lg:placeholder-[#231F20]/30 focus:outline-none focus:border-[#0097AC] lg:focus:border-[#006E74] focus:ring-4 focus:ring-[#0097AC]/10 lg:focus:ring-[#006E74]/5 transition-all invalid:border-red-500/40"
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-[#006E74] hover:bg-[#0097AC] disabled:bg-white/10 lg:disabled:bg-slate-200 disabled:text-white/20 lg:disabled:text-[#231F20]/40 text-white font-bold py-2.5 rounded-xl transition-all shadow-md shadow-black/20 lg:shadow-[#006E74]/10 flex items-center justify-center gap-2 mt-6 cursor-pointer text-sm"
            >
              {loading ? (
                <>
                  <Loader size={16} className="animate-spin text-white" />
                  <span className="font-semibold">Validating Credentials</span>
                </>
              ) : (
                "Login to Your Account"
              )}
            </button>

          </form>

        </div>
        
        <div className="block lg:hidden text-[10px] text-white/20 font-mono mt-8">
          <div>
              © {new Date().getFullYear()}{" "}
              <span className="font-semibold ">
                UST Global
              </span>{" "}
              • All rights reserved
            </div>
        </div>
        
      </div>
    </div>
  );
}