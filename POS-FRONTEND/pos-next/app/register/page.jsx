"use client";

import React, { useEffect, useState } from "react";
import axios from "axios";
import { useRouter } from "next/navigation";

function Register() {
  const [rolesList, setRolesList] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPw, setShowPw] = useState(false);

  const router = useRouter();

  const [user, setUser] = useState({
    name: "",
    username: "",
    password: "",
    phoneNo: "",
    roles: [],
  });

  useEffect(() => { fetchRoles(); }, []);

  const fetchRoles = async () => {
    try {
      const res = await axios.post("/api/role/list",
        {
          page: 0,
          sizePerPage: 10,
          sortDirection: "ASC",
          sortField: "identifier",
        }
      );

      setRolesList(res.data.dtoList || []);
    } catch (err) {
      console.error("Failed to load roles", err);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "phoneNo") {
      setUser({ ...user, [name]: value.replaceAll(/\D/g, ""), });
    } else {
      setUser({ ...user, [name]: value, });
    }
    setError("");
  };

  const handleRoleChange = (roleIdentifier) => {
    const alreadySelected = user.roles.includes(roleIdentifier);

    setUser({
      ...user, roles: alreadySelected
        ? user.roles.filter((r) => r !== roleIdentifier)
        : [...user.roles, roleIdentifier],
    });
    setError("");
  };

  const pwRules = [
    {
      label: "6+ characters",
      met: user.password.length >= 6,
    },
    {
      label: "1 uppercase",
      met: /[A-Z]/.test(user.password),
    },
    {
      label: "1 number",
      met: /\d/.test(user.password),
    },
  ];

  const validate = () => {
    if (!user.name.trim()) {
      return "Full name is required.";
    }
    
    if (!user.username.trim()) {
      return "Email is required.";
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(user.username)) {
      return "Enter a valid email address.";
    }

    if (!/^\d{10}$/.test(user.phoneNo)) {
      return "Phone number must be exactly 10 digits.";
    }

    if (user.roles.length === 0) {
      return "Please select at least one role.";
    }

    if (!pwRules.every((r) => r.met)) {
      return "Password does not meet all requirements.";
    }
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const err = validate();
    if (err) {
      setError(err);
      return;
    }
    setLoading(true);
    setError("");
    try {
      const res = await axios.post("/api/user/register", user);

      if (res.data.success === false) {
        setError(res.data.message);
        return;
      }
      router.push("/login");

    } catch (err) {
      if (err.response?.status === 409) {
        setError("Email already registered.");
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError("Registration failed. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  const steps = [
    { num: 1, title: "Account details", sub: "Name & email", active: true },
    { num: 2, title: "Access & role", sub: "Assign permissions", active: false },
    { num: 3, title: "Security", sub: "Phone & password", active: false },];

  return (
    <>
      <style>{`@keyframes fadeIn {from {opacity: 0; transform: translateY(16px);} to {opacity: 1; transform: translateY(0);}}.fade-in {animation: fadeIn 0.4s ease;}
      `}</style>

      <div className="min-h-screen bg-[#e8eaf0] flex items-center justify-center px-4 py-10">
        <div className="fade-in flex w-full max-w-225 rounded-2xl overflow-hidden shadow-[0_8px_40px_rgba(0,0,0,0.18)]">
          <div className="w-55 min-w-55 bg-[#1e2433] flex flex-col justify-between p-7">
            <div>
              <div className="flex items-center gap-3 mb-10">
                <div>
                  <div className="text-white text-sm font-semibold leading-tight">
                    POS System
                  </div>
                  <div className="text-[#64748b] text-xs">
                    Staff Portal
                  </div>
                </div>
              </div>
              <div className="flex flex-col gap-5">
                {steps.map((step) => (
                  <div
                    key={step.num}
                    className="flex items-start gap-3"
                  >
                    <div
                      className={"w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold shrink-0 mt-0.5 " + (step.active ? "bg-[#3b82f6] text-white": "bg-[#2d3748] text-[#4a5568]")}
                    >
                      {step.num}
                    </div>

                    <div>
                      <div
                        className={"text-sm font-medium " + (step.active ? "text-white" : "text-[#4a5568]")
                        }
                      >
                        {step.title}
                      </div>

                      <div
                        className={"text-xs " + (step.active ? "text-[#94a3b8]" : "text-[#374151]")}
                      >
                        {step.sub}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
            <div className="text-[11px] text-[#374151] leading-relaxed">
              Need help? Contact your system administrator.
            </div>
          </div>

          <div className="flex-1 bg-white p-10">
            <h2 className="text-[22px] font-bold text-[#0f172a] mb-1">
              New staff account
            </h2>
            <p className="text-sm text-[#64748b] mb-7">
              Fill in the details to register a POS user
            </p>
            {error && (
              <div className="bg-[#fef2f2] border border-[#fecaca] text-[#dc2626] px-4 py-3 rounded-lg text-sm mb-5">
                {error}
              </div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="grid grid-cols-2 gap-4 mb-5">
                <div>
                  <label htmlFor="name" className="block text-[11px] font-semibold text-[#64748b] uppercase tracking-wider mb-2">
                    Full Name
                  </label>
                  <input
                    id="name"
                    type="text"
                    name="name"
                    value={user.name}
                    onChange={handleChange}
                    placeholder="Jane Doe"
                    required
                    className="w-full h-11 border border-[#e2e8f0] rounded-lg px-3 text-sm text-[#0f172a] placeholder-[#cbd5e1] bg-white focus:outline-none focus:border-[#3b82f6] focus:shadow-[0_0_0_3px_rgba(59,130,246,0.12)] transition-all duration-200"
                  />
                </div>

                <div>
                  <label htmlFor="username" className="block text-[11px] font-semibold text-[#64748b] uppercase tracking-wider mb-2">
                    Email
                  </label>
                  <input
                    id="username"
                    type="email"
                    name="username"
                    value={user.username}
                    onChange={handleChange}
                    placeholder="jane@store.com"
                    required
                    className="w-full h-11 border border-[#e2e8f0] rounded-lg px-3 text-sm text-[#0f172a] placeholder-[#cbd5e1] bg-white focus:outline-none focus:border-[#3b82f6] focus:shadow-[0_0_0_3px_rgba(59,130,246,0.12)] transition-all duration-200"
                  />
                </div>

              </div>

              <div className="mb-5">
                <div className="block text-[11px] font-semibold text-[#64748b] uppercase tracking-wider mb-2">
                  Roles
                </div>
                <div className="flex flex-wrap gap-2">
                  {rolesList.map((role) => {
                    const selected = user.roles.includes(role.identifier);
                    return (
                      <button
                        key={role.identifier}
                        type="button"
                        onClick={() =>
                          handleRoleChange(role.identifier)
                        }
                        className={
                          "px-4 py-1.5 rounded-full text-sm font-medium border transition-all duration-200 " +
                          (selected
                            ? "bg-[#3b82f6] border-[#3b82f6] text-white"
                            : "bg-white border-[#e2e8f0] text-[#475569] hover:border-[#3b82f6] hover:text-[#3b82f6]")
                        }
                      >
                        {role.identifier}
                      </button>
                    );
                  })}
                </div>

                {user.roles.length === 0 && (
                  <p className="text-xs text-[#94a3b8] mt-2">
                    Select one or more roles
                  </p>
                )}
              </div>

              <div className="grid grid-cols-2 gap-4 mb-5">
                <div>
                  <label htmlFor="phoneNo" className="block text-[11px] font-semibold text-[#64748b] uppercase tracking-wider mb-2">
                    Phone Number
                  </label>
                  <input
                    id="phoneNo"
                    type="text"
                    name="phoneNo"
                    value={user.phoneNo}
                    onChange={handleChange}
                    placeholder="10-digit number"
                    maxLength={10}
                    required
                    className="w-full h-11 border border-[#e2e8f0] rounded-lg px-3 text-sm text-[#0f172a] placeholder-[#cbd5e1] bg-white focus:outline-none focus:border-[#3b82f6] focus:shadow-[0_0_0_3px_rgba(59,130,246,0.12)] transition-all duration-200"
                  />
                </div>

                <div>
                  <label htmlFor="password" className="block text-[11px] font-semibold text-[#64748b] uppercase tracking-wider mb-2">
                    Password
                  </label>
                  <div className="relative">
                    <input
                      id="password"
                      type={showPw ? "text" : "password"}
                      name="password"
                      value={user.password}
                      onChange={handleChange}
                      placeholder="Create a password"
                      required
                      className="w-full h-11 border border-[#e2e8f0] rounded-lg pl-3 pr-10 text-sm text-[#0f172a] placeholder-[#cbd5e1] bg-white focus:outline-none focus:border-[#3b82f6] focus:shadow-[0_0_0_3px_rgba(59,130,246,0.12)] transition-all duration-200"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPw(!showPw)}
                      className="absolute top-1/2 right-3 -translate-y-1/2 text-[#94a3b8] text-sm"
                    >
                      {showPw ? "Hide" : "Show"}
                    </button>
                  </div>
                </div>
              </div>

              {user.password.length > 0 && (
                <div className="flex gap-2 mb-5 flex-wrap">
                  {pwRules.map((rule) => (
                    <span
                      key={rule.label}
                      className={
                        "px-3 py-1 rounded-full text-xs font-medium border transition-all duration-200 " +
                        (rule.met
                          ? "bg-[#ecfdf5] text-[#16a34a] border-[#bbf7d0]"
                          : "bg-[#f8fafc] text-[#94a3b8] border-[#e2e8f0]")
                      }
                    >
                      {rule.met ? "✓ " : ""}
                      {rule.label}
                    </span>
                  ))}
                </div>
              )}

              <div className="flex items-center justify-between mt-6">
                <p className="text-sm text-[#64748b]">
                  Already registered?{" "}
                  <button
                    type="button"
                    onClick={() => router.push("/login")}
                    className="text-[#3b82f6] font-semibold hover:underline bg-transparent border-none cursor-pointer"
                  >
                    Login
                  </button>
                </p>
                <button
                  type="submit"
                  disabled={loading}
                  className="bg-[#3b82f6] hover:bg-[#2563eb] text-white text-sm font-semibold px-6 py-2.5 rounded-lg border-none cursor-pointer transition-all duration-200 disabled:opacity-60 disabled:cursor-not-allowed"
                >
                  {loading ? "Creating..." : "Create account →"}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </>
  );
}

export default Register;