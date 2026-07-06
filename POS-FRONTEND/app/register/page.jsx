"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";

import axiosInstance from "../services/axiosInstance";

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

const fetchRoles = async () => {
  try {
    const res = await axiosInstance.post("/role/list", {
      page: 0,
      sizePerPage: 10,
      sortDirection: "ASC",
      sortField: "identifier",
    });

    console.log("Role API Response:", res.data);

    setRolesList(res.data.dtoList || res.data || []);
  } catch (err) {
    console.error("Failed to load roles", err);
  }
};

const handleChange = (e) => {
  const { name, value } = e.target;

  if (name === "phoneNo") {
    setUser({
      ...user,
      value.replace(/\D/g, ""),
    });
  } else {
    setUser({
      ...user,
      value,
    });
  }

  setError("");
};

const handleRoleChange = (roleIdentifier) => {
  const alreadySelected = user.roles.includes(roleIdentifier);

  setUser({
    ...user,
    roles: alreadySelected
      ? user.roles.filter((r) => r !== roleIdentifier)
      : [...user.roles, roleIdentifier],
  });

  setError("");
};

  const pwRules = [
    { label: "6+ characters", met: user.password.length >= 6 },
    { label: "1 uppercase", met: /[A-Z]/.test(user.password) },
    { label: "1 number", met: /\d/.test(user.password) },
  ];

  const validate = () => {
    if (!user.name.trim()) return "Full name is required.";
    if (!user.username.trim()) return "Email is required.";
   const username = user.username?.trim();

if (
  !username ||
  username.length > 254 ||
  username.indexOf("@") <= 0 ||
  username.lastIndexOf(".") <= username.indexOf("@") + 1 ||
  username.lastIndexOf(".") === username.length - 1 ||
  username.includes(" ")
) {
  return "Enter a valid email address.";
}
    if (!/^\d{10}$/.test(user.phoneNo))
      return "Phone number must be exactly 10 digits.";
    if (user.roles.length === 0)
      return "Please select at least one role.";
    if (!pwRules.every((r) => r.met))
      return "Password does not meet all requirements.";
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const err = validate();
    if (err) { setError(err); return; }
    setLoading(true);
    try {
      const res = await axiosInstance.post("/user/register", user);
      if (res.data.success === false) { setError(res.data.message); return; }
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

  const inputClass =
    "w-full border border-gray-300 rounded-lg px-4 py-2.5 text-sm text-gray-700 bg-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-red-500 transition-all";

  const labelClass =
    "block text-sm font-medium text-gray-700 mb-1.5";

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center px-4 py-10">
      <div className="w-full max-w-2xl bg-white rounded-2xl shadow-sm overflow-hidden">

        <div className="flex items-center justify-between px-8 py-6 border-b">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-lg bg-red-600 flex items-center justify-center text-white font-bold text-sm">
              N
            </div>
            <span className="text-xl font-semibold text-gray-800">POS</span>
          </div>
          <span className="text-sm text-gray-500">Staff Registration</span>
        </div>

        <div className="px-8 py-8">
          <h2 className="text-2xl font-semibold text-gray-800 mb-1">
            Create New Account
          </h2>
          <p className="text-sm text-gray-500 mb-7">
            Fill in the details to register a POS user
          </p>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-lg text-sm mb-6">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-5 mb-5">
              <div>
                <label className={labelClass} htmlFor="name">
                  Full Name <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  value={user.name}
                  onChange={handleChange}
                  placeholder="your name"
                  className={inputClass}
                />
              </div>

              <div>
                <label className={labelClass} htmlFor="username">
                  Email <span className="text-red-500">*</span>
                </label>
                <input
                  type="email"
                  id="username"
                  name="username"
                  value={user.username}
                  onChange={handleChange}
                  placeholder="example@gmail.com"
                  className={inputClass}
                />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-5 mb-5">
              <div>
                <label className={labelClass} htmlFor="phoneNo">
                  Phone Number <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  id="phoneNo"
                  name="phoneNo"
                  value={user.phoneNo}
                  onChange={handleChange}
                  placeholder="10-digit number"
                  maxLength="10"
                  className={inputClass}
                />
              </div>

              <div>
                <label className={labelClass} htmlFor="password">
                  Password <span className="text-red-500">*</span>
                </label>
                <div className="relative">
                  <input
                    type={showPw ? "text" : "password"}
                    name="password"
                    value={user.password}
                    onChange={handleChange}
                    placeholder="Create a password"
                    className={`${inputClass} pr-10`}
                  />
                  <button
                    type="button"
                    onClick={() => setShowPw(!showPw)}
                    className="absolute top-1/2 right-3 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                  >
                    {showPw ? "🙈" : "👁"}
                  </button>
                </div>
              </div>
            </div>

            {user.password.length > 0 && (
              <div className="flex gap-2 flex-wrap mb-5">
                {pwRules.map((rule) => (
                  <span
                    key={rule.label}
                    className={`px-3 py-1 rounded-full text-xs font-medium border transition-all ${
                      rule.met
                        ? "bg-green-50 text-green-600 border-green-200"
                        : "bg-gray-50 text-gray-400 border-gray-200"
                    }`}
                  >
                    {rule.met ? "✓ " : ""}{rule.label}
                  </span>
                ))}
              </div>
            )}

            <fieldset className="mb-7 border-0 p-0">
              <legend className={labelClass}>
                Roles <span className="text-red-500">*</span>
              </legend>
              <div className="flex flex-wrap gap-2">
                {rolesList.map((role, index) => {
                  const selected = user.roles.includes(role.identifier);
                  return (
                    <button
                      key={role.id || role.identifier || index}
                      type="button"
                      onClick={() => handleRoleChange(role.identifier)}
                      className={`px-4 py-1.5 rounded-full text-sm font-medium border transition-all ${
                        selected
                          ? "bg-red-600 border-red-600 text-white"
                          : "bg-white border-gray-300 text-gray-600 hover:border-red-500 hover:text-red-600"
                      }`}
                    >
                      {role.identifier}
                    </button>
                  );
                })}
              </div>
              {user.roles.length === 0 && (
                <p className="text-xs text-gray-400 mt-2">
                  Select one or more roles
                </p>
              )}
            </fieldset>

            <div className="flex items-center justify-between pt-5 border-t">
              <p className="text-sm text-gray-500">
                Already registered?{" "}
                <Link
                  href="/login"
                  className="text-red-600 font-semibold hover:underline"
                >
                  Login
                </Link>
              </p>

              <button
                type="submit"
                disabled={loading}
                className="flex items-center gap-2 bg-red-600 hover:bg-red-700 text-white text-sm font-medium px-6 py-2.5 rounded-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {loading ? "Creating..." : "Create Account →"}
              </button>
            </div>

          </form>
        </div>
      </div>
    </div>
  );
}

export default Register;
