// app/register/page.jsx

"use client";

import React, { useState, useEffect } from "react";
import axios from "../api/axios";
import { useRouter } from "next/navigation";
import { UserPlus, Eye, EyeOff, Phone, Mail, User, Lock } from "lucide-react";
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
    setUser({ ...user, [e.target.name]: e.target.value });
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
    if (user.roles.length === 0) return "Select at least one terminal security role";
    if (!/^\d{10}$/.test(user.phoneNo)) return "Phone number must be exactly 10 digits";
    if (user.password.length < 6) return "Password must be at least 6 characters long";

    if (!/[A-Z]/.test(user.password)) {
      return "Add at least one uppercase letter (A-Z)";
    }
    if (!/\d/.test(user.password)) {
      return "Add at least one numerical digit (0-9)";
    }
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
    <div className="flex h-screen w-screen bg-[#FFFFFF] font-sans text-left overflow-hidden select-none">

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
          v4.1.0-build || Sprint-4
        </div>
      </div>

      <div className="w-full lg:w-7/12 flex justify-center items-center px-6 sm:px-12 md:px-16 overflow-y-auto bg-slate-50 h-full">
        <div className="w-full max-w-xl bg-white p-8 sm:p-10 rounded-2xl border border-[#231F20]/10 shadow-xl shadow-[#231F20]/5 my-6">

          <div className="flex items-center gap-2 mb-1">
            <UserPlus className="text-[#006E74]" size={22} />
            <h2 className="text-2xl font-bold text-[#231F20] tracking-tight">Create Identity</h2>
          </div>
          <p className="text-xs text-[#231F20]/60 mb-6">Credentials are subjected to Admin's Approval</p>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 text-xs font-semibold p-3.5 rounded-xl mb-5 flex items-center gap-2">
              <span>⚠️</span> {error}
            </div>
          )}
          {successMsg && (
            <div className="bg-[#006E74]/10 border border-[#006E74]/30 text-[#006E74] text-xs font-semibold p-3.5 rounded-xl mb-5 flex items-center gap-2">
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
                  <User size={16} className="absolute left-3 top-3 text-[#231F20]/30" />
                  <input
                    id="name"
                    name="name"
                    type="text"
                    placeholder="Kushal S"
                    value={user.name}
                    onChange={handleChange}
                    className="pl-9 pr-3 py-2.5 border border-[#231F20]/20 bg-white text-[#231F20] rounded-xl w-full text-sm placeholder-[#231F20]/30 focus:outline-none focus:border-[#006E74] focus:ring-4 focus:ring-[#006E74]/5 transition-all"
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
                  <Mail size={16} className="absolute left-3 top-3 text-[#231F20]/30" />
                  <input
                    id="username"
                    name="username"
                    type="email"
                    placeholder="kushal@ust.com"
                    value={user.username}
                    onChange={handleChange}
                    className="pl-9 pr-3 py-2.5 border border-[#231F20]/20 bg-white text-[#231F20] rounded-xl w-full text-sm placeholder-[#231F20]/30 focus:outline-none focus:border-[#006E74] focus:ring-4 focus:ring-[#006E74]/5 transition-all"
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
                  <Phone size={16} className="absolute left-3 top-3 text-[#231F20]/30" />
                  <input
                    id="phoneNo"
                    name="phoneNo"
                    type="text"
                    placeholder="10-digit mobile"
                    value={user.phoneNo}
                    onChange={handleChange}
                    className="pl-9 pr-3 py-2.5 border border-[#231F20]/20 bg-white text-[#231F20] rounded-xl w-full text-sm placeholder-[#231F20]/30 focus:outline-none focus:border-[#006E74] focus:ring-4 focus:ring-[#006E74]/5 transition-all"
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
                  <Lock size={16} className="absolute left-3 top-3 text-[#231F20]/30" />
                  <input
                    id="register-password-input"
                    name="password"
                    type={showPassword ? "text" : "password"}
                    placeholder="••••••••"
                    value={user.password}
                    onChange={handleChange}
                    className="pl-9 pr-10 py-2.5 border border-[#231F20]/20 bg-white text-[#231F20] rounded-xl w-full text-sm placeholder-[#231F20]/30 focus:outline-none focus:border-[#006E74] focus:ring-4 focus:ring-[#006E74]/5 transition-all"
                    required
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-3 text-[#231F20]/40 hover:text-[#231F20] transition-colors cursor-pointer"
                  >
                    {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
              </div>
            </div>

            <div>
              <label
                htmlFor="roles-selection-container"
                className="text-[10px] font-bold uppercase tracking-widest text-[#231F20]/60 block mb-2"
              >
                Assign User Roles
              </label>

              <div
                id="roles-selection-container"
                className="grid sm:grid-cols-2 gap-2 max-h-[150px] overflow-y-auto pr-1 border border-[#231F20]/10 p-2 rounded-xl bg-slate-50"
              >
                {rolesList.map((role, idx) => {
                  const isActive = user.roles.includes(role.identifier);

                  return (
                    <button
                      key={role.id || role.identifier || `role-idx-${idx}`}
                      type="button"
                      onClick={() => toggleRole(role.identifier)}
                      className={`p-2.5 border rounded-lg flex gap-2 items-start transition-all duration-150
                        ${isActive
                          ? "bg-[#006E74]/5 border-[#006E74] shadow-sm"
                          : "bg-white border-[#231F20]/10 hover:border-[#231F20]/30"}
                      `}
                    >
                      <div className={`w-3.5 h-3.5 rounded border flex items-center justify-center text-[8px] font-bold mt-0.5 transition-all
                        ${isActive ? "bg-[#006E74] border-[#006E74] text-white" : "border-[#231F20]/20 bg-white"}
                      `}>
                        {isActive && "✓"}
                      </div>

                      <div className="flex-1 min-w-0">
                        <p className="text-xs font-bold text-[#231F20] truncate">{role.identifier}</p>
                      </div>
                    </button>
                  );
                })}
              </div>
            </div>

            <button
              type="submit"
              className="w-full bg-[#006E74] hover:bg-[#0097AC] text-white font-bold text-sm py-2.5 rounded-xl transition-colors shadow-md shadow-[#006E74]/10 cursor-pointer mt-2"
            >
              Create Account
            </button>
          </form>

          <p className="text-center text-xs text-[#231F20]/60 mt-5">
            Already registered ?{" "}
            <button
              onClick={() => router.push("/login")}
              className="text-[#006E74] hover:text-[#0097AC] font-bold underline transition-colors cursor-pointer"
            >
              Login here
            </button>
          </p>

        </div>
      </div>
    </div>
  );
}

export default Register;