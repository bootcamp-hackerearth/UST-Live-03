"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";

export default function LoginForm() {

  const router = useRouter();

  const [credentials, setCredentials] = useState({username: "", password: "",});

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {

    setCredentials({
      ...credentials,
      [e.target.name]: e.target.value,
    });

    setError("");
  };

 const handleSubmit = async (e) => {e.preventDefault();
  setLoading(true);
  setError("");

  try {
    const response = await fetch("/api/login", {
      method: "POST",
      headers: {"Content-Type": "application/json",
      },
      body: JSON.stringify(credentials),
    });

    const data = await response.json();
  
    if (!response.ok) {
      console.log("LOGIN FAILED");
      setError(data.message || "Login failed");
      return;
    }

    localStorage.setItem("userEmail", credentials.username);
    router.push("/home");

  } catch (err) {
    console.error(err);
    setError("Something went wrong");
  } finally {
    setLoading(false);
  }
};

  return (
  <div className="min-h-screen flex items-center justify-center bg-[#d9dcf3] p-6">
    <div className="relative w-full max-w-6xl overflow-hidden rounded-4xl bg-[#f4f5fb] shadow-2xl">
      <div className="absolute top-4 right-6 flex gap-2">
        <div className="h-2 w-2 rounded-full bg-[#9c8cf5]" />
        <div className="h-2 w-2 rounded-full bg-[#8ad6ff]" />
      </div>
      <div className="m-6 overflow-hidden rounded-3xl bg-[#45248d]">
        <div className="grid min-h-162.5 lg:grid-cols-2">
          <div className="relative flex flex-col justify-center px-14 py-16">
            <div className="mb-10">
              <div className="relative h-48 w-64">
                <div className="absolute top-0 left-0 h-24 w-52 rounded-full bg-linear-to-r from-[#77d8ff] to-[#7e6dff] opacity-90 blur-sm" />
                <div className="absolute top-5 left-4 h-24 w-52 rounded-full bg-linear-to-r from-[#8ee3ff] to-[#9a7dff]" />
                <div className="absolute top-10 left-8 h-24 w-52 rounded-full bg-linear-to-r from-[#63cfff] to-[#6f52f5]" />
                <div className="absolute top-16 left-12 h-24 w-52 rounded-full bg-linear-to-r from-[#54bfff] to-[#5030d6]" />
              </div>
            </div>

            <div className="max-w-sm">
              <h1 className="mb-3 text-4xl font-bold text-white">
                POS System
              </h1>
              <p className="text-lg leading-relaxed text-purple-100">
                Secure access to your Point of Sale management platform.
              </p>
              <div className="mt-10 flex gap-4">
                <button
                  type="button"
                  className="rounded-full bg-white/10 px-5 py-2 text-sm font-medium text-white backdrop-blur"
                >
                  What is POS?
                </button>
                <button
                  type="button"
                  className="rounded-full border border-white/20 px-5 py-2 text-sm font-medium text-white"
                >
                  Learn More
                </button>
              </div>
            </div>
          </div>

          <div className="flex items-center justify-center p-10">
            <div className="w-full max-w-md rounded-3xl bg-[#5631a6]/60 p-8 backdrop-blur-sm">
              <h2 className="mb-8 text-3xl font-bold text-white">
                Log In
              </h2>
              {error && (
                <div className="mb-5 rounded-xl border border-red-400/30 bg-red-500/10 px-4 py-3 text-sm text-red-200">
                  {error}
                </div>
              )}

              <form onSubmit={handleSubmit}>
                <div className="space-y-5">
                  <div>
                    <label htmlFor="username" className="mb-2 block text-sm text-purple-100">
                      Email
                    </label>
                    <input
                      id="username"
                      name="username"
                      type="email"
                      value={credentials.username}
                      onChange={handleChange}
                      placeholder="Enter email"
                      required
                      className="h-12 w-full rounded-xl border border-white/10 bg-white/10 px-4 text-white placeholder:text-purple-200 focus:outline-none focus:ring-2 focus:ring-[#9be9ff]"
                    />
                  </div>

                  <div>
                    <label htmlFor="password" className="mb-2 block text-sm text-purple-100">
                      Password
                    </label>
                    <input
                      id="password"
                      name="password"
                      type="password"
                      value={credentials.password}
                      onChange={handleChange}
                      placeholder="Enter password"
                      required
                      className="h-12 w-full rounded-xl border border-white/10 bg-white/10 px-4 text-white placeholder:text-purple-200 focus:outline-none focus:ring-2 focus:ring-[#9be9ff]"
                    />
                  </div>
                  <button
                    type="submit"
                    disabled={loading}
                    className="mt-3 h-12 w-full rounded-xl bg-[#9be9ff] font-semibold text-[#3d2388] transition hover:bg-[#82dfff] disabled:opacity-60"
                  >
                    {loading ? "Logging in..." : "Log In"}
                  </button>
                  <button
                    type="button"
                    onClick={() => router.push("/register")}
                    className="h-12 w-full rounded-xl border border-white/15 text-white transition hover:bg-white/5"
                  >
                    Sign Up
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
);
}
