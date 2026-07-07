// app/register/page.jsx

"use client";

import React, { useState, useEffect } from "react";
import axios from "../api/axios";
import { useRouter } from "next/navigation";
import { UserPlus, Eye, EyeOff, Phone, Mail, User, Lock, Layers } from "lucide-react";
import ustLogo from "@/assets/logo/UST-White-logo.png";

function Register() {
  const [rolesList, setRolesList] = useState([]);
  const [error, setError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const router = useRouter();

  const [user, setUser] = useState({
    name: "",
    username: "",
    password: "",
    phoneNo: "",
    roles: [],
  });

  useEffect(() => {
    fetchRoles();
  }, []);

  const fetchRoles = async () => {
    try {
      const response = await axios.get("/role/getAllActive");
      setRolesList(response.data.content || response.data);
    } catch (err) {
      if (err.message) {
        setError("Unable to complete network handshakes securely.");
      }
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "phoneNo") {
      const cleanedValue = value.replaceAll(/\D/g, "");
      if (cleanedValue.length <= 10) {
        setUser((prev) => ({ ...prev, [name]: cleanedValue }));
      }
    } else {
      setUser((prev) => ({ ...prev, [name]: value }));
    }
    setError("");
  };

  const toggleRole = (roleName) => {
    const updatedRoles = user.roles.includes(roleName)
      ? user.roles.filter((r) => r !== roleName)
      : [...user.roles, roleName];

    setUser({ ...user, roles: updatedRoles });
    setError("");
  };

  const validate = () => {
    if (!user.name.trim() || user.name.trim().length < 2) {
      return "Please enter a valid full name (minimum 2 characters)";
    }

    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(user.username)) {
      return "Please enter a valid corporate email address";
    }

    if (!/^[6-9]\d{9}$/.test(user.phoneNo)) {
      return "Phone number must be 10 digits and start with a valid prefix (6-9)";
    }

    if (user.password.length < 8) return "Password must be at least 8 characters long";
    if (!/[A-Z]/.test(user.password)) return "Password requires at least one uppercase letter (A-Z)";
    if (!/[a-z]/.test(user.password)) return "Password requires at least one lowercase letter (a-z)";
    if (!/\d/.test(user.password)) return "Password requires at least one numerical digit (0-9)";
    if (!/[!@#$%^&*(),.?":{}|<>]/.test(user.password)) {
      return "Password requires at least one special character (e.g., @, #, $, %)";
    }

    if (user.roles.length === 0) return "Select at least one terminal security role";

    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const validationError = validate();
    if (validationError) {
      setError(validationError);
      return;
    }

    try {
      const res = await axios.post("/user/register", user);
      if (res.data?.success === false) {
        setError(res.data.message || "Registration failed");
        return;
      }
      setError("");
      setSuccessMsg("Account provisions initialized. Redirecting...");
      setTimeout(() => {
        router.push("/login");
      }, 1500);
    } catch (err) {
      let message = "Registration failed";
      if (err.response) {
        message = err.response.data?.message || `Error ${err.response.status}: Request failed`;
      } else if (err.request) {
        message = "Server not responding. Please try again later.";
      } else if (err.message) {
        message = err.message;
      }
      setError(message);
    }
  };

  return (
    <div className="flex h-screen w-screen bg-[#231F20] lg:bg-[#FFFFFF] overflow-x-hidden overflow-y-auto lg:overflow-hidden select-none font-sans text-left">
      <div className="hidden lg:flex w-5/12 bg-[#231F20] text-white flex-col p-12 justify-between relative border-r border-white/10">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 rounded-xl flex items-center justify-center overflow-hidden">
            <img
              src={ustLogo.src}
              alt="UST Logo"
              className="w-10 h-10 object-contain"
            />
          </div>
          <div className="leading-none">
            <span className="text-lg font-bold tracking-wider uppercase text-white block">UST Retail POS</span>
            <span className="text-[11px] text-[#0097AC] font-semibold tracking-widest uppercase">Built for Ease</span>
          </div>
        </div>

        <div className="my-auto max-w-sm">
          <h1 className="text-4xl font-extrabold tracking-tight text-white leading-tight">
            Run your store terminal with <span className="text-[#0097AC]">absolute control.</span>
          </h1>
          <p className="text-xs text-white/60 mt-4 leading-relaxed">
            Onboard point-of-sale operators and backend managers instantly using high-granularity role security parameters tied to your enterprise matrix.
          </p>
          <div className="mt-8 flex gap-2">
            <div className="h-1.5 w-12 bg-[#006E74] rounded-full" />
            <div className="h-1.5 w-4 bg-[#0097AC] rounded-full" />
          </div>
        </div>

        <div className="text-[10px] text-white/30 font-mono tracking-wider">
          v5.1.0-build || Sprint-5
        </div>
      </div>

      <div className="w-full lg:w-7/12 min-h-full flex flex-col justify-center items-center px-4 sm:px-12 md:px-16 py-8 relative bg-gradient-to-b from-[#231F20] via-[#2d2829] to-[#1a1718] lg:from-slate-50 lg:via-slate-50 lg:to-slate-50 overflow-y-auto">
        
        <div className="absolute top-[-5%] left-[-10%] w-[350px] h-[350px] rounded-full bg-[#0097AC]/5 blur-[90px] pointer-events-none lg:hidden" />
        <div className="absolute bottom-[-5%] right-[-10%] w-[350px] h-[350px] rounded-full bg-[#006E74]/5 blur-[90px] pointer-events-none lg:hidden" />

        <div className="w-full max-w-xl flex flex-col z-10 my-auto">
          
          <div className="flex lg:hidden flex-col items-center text-center mb-6">
            <div className="p-3 bg-white/5 border border-white/10 rounded-2xl shadow-xl backdrop-blur-md mb-3">
              <img
                src={ustLogo.src}
                alt="UST Logo"
                className="w-12 h-12 object-contain"
              />
            </div>
            <h1 className="text-2xl font-black text-white tracking-tight">
              UST <span className="text-[#0097AC]">Retail POS</span>
            </h1>
            <p className="text-[10px] text-white/40 tracking-widest uppercase font-semibold mt-1">
              Enterprise Identity Gateway
            </p>
          </div>

          <div className="w-full bg-white rounded-3xl shadow-2xl lg:shadow-xl shadow-black/40 lg:shadow-[#231F20]/5 border border-white/10 lg:border-[#231F20]/10 p-6 sm:p-10 backdrop-blur-lg lg:backdrop-blur-none">

            <div className="flex items-center gap-2 mb-1">
              <div className="p-1.5 rounded-lg bg-[#006E74]/10 lg:bg-[#006E74]/5">
                <UserPlus className="text-[#006E74]" size={20} />
              </div>
              <h2 className="text-xl sm:text-2xl font-bold text-[#231F20] tracking-tight">Create Identity</h2>
            </div>
            <p className="text-xs text-[#231F20]/50 mb-6">Credentials are subject to Admin approval.</p>

            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 text-xs font-semibold p-3.5 rounded-xl mb-5 flex items-center gap-2 shadow-sm">
                <span>⚠️</span> {error}
              </div>
            )}
            {successMsg && (
              <div className="bg-[#006E74]/10 border border-[#006E74]/30 text-[#006E74] text-xs font-semibold p-3.5 rounded-xl mb-5 flex items-center gap-2 shadow-sm">
                <span>✓</span> {successMsg}
              </div>
            )}

          <form onSubmit={handleSubmit} className="space-y-4">

            <div className="grid sm:grid-cols-2 gap-4">
              <div>
                <label
                  htmlFor="name"
                  className="text-[10px] font-bold uppercase tracking-widest text-[#231F20]/60 block mb-1.5"
                >
                  Full Name
                </label>
                <div className="relative">
                  <User size={16} className="absolute left-3.5 top-3.5 text-[#231F20]/30" />
                  <input
                    id="name"
                    name="name"
                    type="text"
                    minLength="2"
                    maxLength="50"
                    placeholder="Kushal S"
                    value={user.name}
                    onChange={handleChange}
                    className="pl-10 pr-4 py-3 bg-slate-50 lg:bg-white text-[#231F20] border border-[#231F20]/15 rounded-xl w-full text-sm placeholder-[#231F20]/30 focus:outline-none focus:border-[#006E74] focus:ring-4 focus:ring-[#006E74]/5 transition-all invalid:border-red-500/40"
                    required
                  />
                </div>
              </div>

              <div>
                <label
                  htmlFor="username"
                  className="text-[10px] font-bold uppercase tracking-widest text-[#231F20]/60 block mb-1.5"
                >
                  User Email
                </label>
                <div className="relative">
                  <Mail size={16} className="absolute left-3.5 top-3.5 text-[#231F20]/30" />
                  <input
                    id="username"
                    name="username"
                    type="email"
                    placeholder="kushal@ust.com"
                    value={user.username}
                    onChange={handleChange}
                    className="pl-10 pr-4 py-3 bg-slate-50 lg:bg-white text-[#231F20] border border-[#231F20]/15 rounded-xl w-full text-sm placeholder-[#231F20]/30 focus:outline-none focus:border-[#006E74] focus:ring-4 focus:ring-[#006E74]/5 transition-all invalid:border-red-500/40"
                    required
                  />
                </div>
              </div>
            </div>

            <div className="grid sm:grid-cols-2 gap-4">
              <div>
                <label
                  htmlFor="phoneNo"
                  className="text-[10px] font-bold uppercase tracking-widest text-[#231F20]/60 block mb-1.5"
                >
                  Phone Number
                </label>
                <div className="relative">
                  <Phone size={16} className="absolute left-3.5 top-3.5 text-[#231F20]/30" />
                  <input
                    id="phoneNo"
                    name="phoneNo"
                    type="text"
                    inputMode="numeric"
                    maxLength="10"
                    pattern="[6-9][0-9]{9}"
                    placeholder="10-digit mobile"
                    value={user.phoneNo}
                    onChange={handleChange}
                    className="pl-10 pr-4 py-3 bg-slate-50 lg:bg-white text-[#231F20] border border-[#231F20]/15 rounded-xl w-full text-sm placeholder-[#231F20]/30 focus:outline-none focus:border-[#006E74] focus:ring-4 focus:ring-[#006E74]/5 transition-all invalid:border-red-500/40"
                    required
                  />
                </div>
              </div>

              <div>
                <label
                  htmlFor="register-password-input"
                  className="text-[10px] font-bold uppercase tracking-widest text-[#231F20]/60 block mb-1.5"
                >
                  System Password
                </label>
                <div className="relative">
                  <Lock size={16} className="absolute left-3.5 top-3.5 text-[#231F20]/30" />
                  <input
                    id="register-password-input"
                    name="password"
                    type={showPassword ? "text" : "password"}
                    placeholder="••••••••"
                    value={user.password}
                    onChange={handleChange}
                    className="pl-10 pr-10 py-3 bg-slate-50 lg:bg-white text-[#231F20] border border-[#231F20]/15 rounded-xl w-full text-sm placeholder-[#231F20]/30 focus:outline-none focus:border-[#006E74] focus:ring-4 focus:ring-[#006E74]/5 transition-all invalid:border-red-500/40"
                    required
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3.5 top-3.5 text-[#231F20]/40 hover:text-[#231F20] transition-colors cursor-pointer"
                  >
                    {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
              </div>
            </div>

            <div>
              <label
                htmlFor="roles-selection-container"
                className="text-[10px] font-bold uppercase tracking-widest text-[#231F20]/60 flex items-center gap-1.5 mb-2"
              >
                <Layers size={12} className="text-[#006E74]" /> Assign User Roles
              </label>

              <div
                id="roles-selection-container"
                className="grid sm:grid-cols-2 gap-2 max-h-[140px] overflow-y-auto pr-1 border border-[#231F20]/10 p-2.5 rounded-xl bg-slate-50/50"
              >
                {rolesList.map((role, idx) => {
                  const isActive = user.roles.includes(role.identifier);

                  return (
                    <button
                      key={role.id || role.identifier || `role-idx-${idx}`}
                      type="button"
                      onClick={() => toggleRole(role.identifier)}
                      className={`p-2.5 border rounded-xl flex gap-2.5 items-center transition-all duration-150 cursor-pointer
                        ${isActive
                          ? "bg-[#006E74]/5 border-[#006E74] shadow-sm"
                          : "bg-white border-[#231F20]/15 hover:border-[#231F20]/30"}
                      `}
                    >
                      <div className={`w-4 h-4 rounded border flex items-center justify-center text-[9px] font-bold transition-all shrink-0
                        ${isActive ? "bg-[#006E74] border-[#006E74] text-white" : "border-[#231F20]/25 bg-white"}
                      `}>
                        {isActive && "✓"}
                      </div>

                      <div className="flex-1 min-w-0 text-left">
                        <p className="text-xs font-bold text-[#231F20] truncate">{role.identifier}</p>
                      </div>
                    </button>
                  );
                })}
              </div>
            </div>

            <button
              type="submit"
              className="w-full bg-[#006E74] hover:bg-[#0097AC] text-white font-bold h-11 rounded-xl transition-colors shadow-lg shadow-[#006E74]/10 cursor-pointer mt-2"
            >
              Create Account
            </button>
          </form>

          <p className="text-center text-xs text-[#231F20]/60 mt-5">
            Already registered?{" "}
            <button
              onClick={() => router.push("/login")}
              className="text-[#006E74] hover:text-[#0097AC] font-bold underline transition-colors cursor-pointer"
            >
              Login here
            </button>
          </p>

          </div>
          
          <div className="mt-8 text-center text-[10px] text-white/20 font-mono tracking-widest block lg:hidden">
            <div>
              © {new Date().getFullYear()}{" "}
              <span className="font-semibold ">
                UST-Global
              </span>{" "}
              • All rights reserved
            </div>
          </div>

        </div>
      </div>
    </div>
  );
}

export default Register;