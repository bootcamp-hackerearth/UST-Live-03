"use client";

import { useState, useEffect } from "react";
import axios from "axios";
import Link from "next/link";
import { useRouter } from "next/navigation";

import {
  UserIcon,
  EnvelopeIcon,
  PhoneIcon,
  LockClosedIcon,
  ShieldCheckIcon,
} from "@heroicons/react/24/outline";

export default function Register() {
  const router = useRouter();

  const [user, setUser] = useState({
    name: "",
    username: "",
    roles: "",
    phoneNo: "",
    password: "",
  });

  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const [roles, setRoles] = useState([]);

  useEffect(() => {
    axios
      .post("/api/role/list", {
        page: 0,
        sizePerPage: 100,
        sortDirection: "ASC",
        sortField: "identifier",
      })
      .then((res) => {
        console.log("Roles Response:", res.data);

        const roleList = Array.isArray(res.data)
          ? res.data
          : res.data.dtoList || res.data.content || [];

        setRoles(roleList);
      })
      .catch((err) => {
        console.log(err);
        setRoles([]);
      });
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    setSuccess("");
    setError("");

    if (!user.name.trim()) {
      setError("Full Name is required");
      return;
    }

    if (!user.username.trim()) {
      setError("Email is required");
      return;
    }

    if (!user.roles) {
      setError("Please select a role");
      return;
    }

    if (!user.phoneNo) {
      setError("Phone number is required");
      return;
    }

    if (!user.password) {
      setError("Password is required");
      return;
    }

    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    const phoneRegex = /^\d{10}$/;
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d).{6,}$/;

    if (!emailRegex.test(user.username)) {
      setError("Enter valid email");
      return;
    }

    if (!phoneRegex.test(user.phoneNo)) {
      setError("Phone number must be 10 digits");
      return;
    }

    if (!passwordRegex.test(user.password)) {
      setError("Password must contain letters and numbers");
      return;
    }

    try {
      const listRes = await axios.post(
        "/api/user/list",
        {
          page: 0,
          sizePerPage: 1000,
        }
      );

      const users =
        Array.isArray(listRes.data)
          ? listRes.data
          : listRes.data.dtoList || listRes.data.content || [];

      const exists = users.some(
        (u) =>
          u.username?.toLowerCase() === user.username.toLowerCase()
      );

      if (exists) {
        setError("Email already exists");
        return;
      }

      const res = await axios.post(
        "/api/user/register",
        {
          name: user.name,
          username: user.username,
          phoneNo: user.phoneNo,
          password: user.password,
          roles: [user.roles],
        }
      );

      console.log("Register Response:", res.data);

      setSuccess("Registration successful");

      setUser({
        name: "",
        username: "",
        roles: "",
        phoneNo: "",
        password: "",
      });

      setTimeout(() => {
        router.push("/login");
      }, 1500);

    } catch (err) {
      console.log("Error:", err.response);
      setError("Registration failed. Please try again.");
    }
  };

  const handleChange = (e) => {
    setUser({
      ...user,
      [e.target.name]: e.target.value,
    });
  };

  return (
    <div className="min-h-screen bg-[#F3F3F3] overflow-hidden flex items-center justify-center relative">
      <div className="absolute left-0 top-0 w-[40%] h-full bg-black rounded-r-[90px]"></div>

      <div className="absolute bottom-[-80px] left-[12%] w-[220px] h-[220px] bg-white rounded-full opacity-90"></div>

      <div className="relative z-10 w-full max-w-6xl grid lg:grid-cols-2 items-center px-8">
        <div className="text-white pl-8">
          <h1 className="text-5xl font-bold leading-[1.05] tracking-[-2px]">
            Create POS
            <br />
            Account
          </h1>

          <p className="text-lg text-gray-300 mt-6 leading-8 max-w-sm">
            Register and manage billing, inventory and retail operations.
          </p>
        </div>

        <div className="relative flex justify-center items-center h-[620px]">
          <div className="absolute rotate-[10deg] scale-95 opacity-60">
            <div className="w-[340px] bg-white rounded-[28px] shadow-[0_15px_60px_rgba(0,0,0,0.12)] p-7">
              <h2 className="text-4xl font-bold text-black mb-8">Login</h2>

              <div className="space-y-4">
                <input
                  type="text"
                  placeholder="Username"
                  className="w-full h-11 rounded-xl border border-gray-300 px-4 text-sm text-black placeholder:text-black outline-none"
                />

                <input
                  type="password"
                  placeholder="Password"
                  className="w-full h-11 rounded-xl border border-gray-300 px-4 text-sm text-black placeholder:text-black outline-none"
                />

                <button className="w-full h-11 rounded-xl bg-black text-white text-sm font-semibold">
                  Login
                </button>
              </div>
            </div>
          </div>

          <div className="relative z-10">
            <div className="w-[360px] bg-white rounded-[30px] shadow-[0_20px_80px_rgba(0,0,0,0.16)] p-8">
              <h2 className="text-5xl font-bold text-black mb-8">Register</h2>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="relative">
                  <UserIcon className="h-4 w-4 absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />

                  <input
                    type="text"
                    name="name"
                    value={user.name}
                    placeholder="Full Name"
                    onChange={handleChange}
                    className="w-full h-11 rounded-xl border border-gray-300 pl-11 pr-4 text-sm text-black placeholder:text-black outline-none focus:border-black"
                  />
                </div>

                <div className="relative">
                  <EnvelopeIcon className="h-4 w-4 absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />

                  <input
                    type="email"
                    name="username"
                    value={user.username}
                    placeholder="Email"
                    onChange={handleChange}
                    className="w-full h-11 rounded-xl border border-gray-300 pl-11 pr-4 text-sm text-black placeholder:text-black outline-none focus:border-black"
                  />
                </div>

                <div className="relative">
                  <ShieldCheckIcon className="h-4 w-4 absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 z-10" />

                  <select
                    name="roles"
                    value={user.roles}
                    onChange={handleChange}
                    className="w-full h-11 rounded-xl border border-gray-300 pl-11 pr-4 text-sm text-black outline-none focus:border-black bg-white"
                  >
                    <option value="">Select Role</option>

                    {(roles || []).map((role) => (
                      <option key={role.id} value={role.identifier}>
                        {role.identifier}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="relative">
                  <PhoneIcon className="h-4 w-4 absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />

                  <input
                    type="tel"
                    name="phoneNo"
                    value={user.phoneNo}
                    placeholder="Phone Number"
                    onChange={handleChange}
                    className="w-full h-11 rounded-xl border border-gray-300 pl-11 pr-4 text-sm text-black placeholder:text-black outline-none focus:border-black"
                  />
                </div>

                <div className="relative">
                  <LockClosedIcon className="h-4 w-4 absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />

                  <input
                    type="password"
                    name="password"
                    value={user.password}
                    placeholder="Password"
                    onChange={handleChange}
                    className="w-full h-11 rounded-xl border border-gray-300 pl-11 pr-4 text-sm text-black placeholder:text-black outline-none focus:border-black"
                  />
                </div>

                {success && <div className="text-green-600 text-sm font-medium">{success}</div>}

                {error && <div className="text-red-500 text-sm font-medium">{error}</div>}

                <button
                  type="submit"
                  className="w-full h-11 rounded-xl bg-black hover:bg-[#1A1A1A] text-white text-sm font-semibold transition-all"
                >
                  Create Account
                </button>
              </form>

              <div className="mt-6 text-center text-gray-500 text-sm">
                Already have an account?{" "}
                <Link href="/login" className="text-black font-semibold">
                  Login
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
