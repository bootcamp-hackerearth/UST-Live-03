"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";

export default function Login() {
  const router = useRouter();

  const [credentials, setCredentials] = useState({
    username: "",
    password: ""
  });

  const [error, setError] = useState("");

  const handleChange = (e) => {
    setCredentials({
      ...credentials,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {

      const res = await fetch("/auth/authenticate", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(credentials)
      });

      const data = await res.json();

      if (!res.ok || !data.token || data.token === "Error") {
        throw new Error("Invalid username or password");
      }

      const cookieRes = await fetch("/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          token: data.token
        })
      });

      if (!cookieRes.ok) {
        throw new Error("Failed to set cookie");
      }

      localStorage.setItem("username", credentials.username);

      router.push("/home");

    } catch (err) {
      console.error("Login failed:", err);
      setError(err.message || "Login failed");
    }

    setCredentials({
      username: "",
      password: ""
    });
  };

  return (
    <div className="min-h-screen bg-linear-to-br from-gray-100 via-gray-200 to-gray-100 flex justify-center items-start pt-20">
      <form
        onSubmit={handleSubmit}
        className="w-65 bg-white p-5 rounded-lg shadow-md relative"
      >
        <div className="absolute top-0 left-0 w-full h-2 bg-linear-to-r from-indigo-500 to-blue-500 rounded-t-lg"></div>

        <h2 className="text-center text-lg font-bold text-gray-900 mb-4">
          Login
        </h2>

        <div className="mb-3">
          <label
            htmlFor="username"
            className="block text-left text-xs font-semibold text-gray-700 mb-1"
          >
            Username
          </label>
          <input
            id="username"
            type="text"
            name="username"
            value={credentials.username}
            onChange={handleChange}
            autoComplete="username"
            required
            className="w-full px-2 py-1.5 text-xs rounded border border-gray-300 bg-gray-100 focus:outline-none focus:ring-2 focus:ring-indigo-300"
          />
        </div>

        <div className="mb-3">
          <label
            htmlFor="password"
            className="block text-left text-xs font-semibold text-gray-700 mb-1"
          >
            Password
          </label>
          <input
            id="password"
            type="password"
            name="password"
            value={credentials.password}
            onChange={handleChange}
            autoComplete="current-password"
            required
            className="w-full px-2 py-1.5 text-xs rounded border border-gray-300 bg-gray-100 focus:outline-none focus:ring-2 focus:ring-indigo-300"
          />
        </div>

        {error && (
          <div className="mb-3 text-center text-red-500 text-xs">
            {error}
          </div>
        )}

        <button
          type="submit"
          className="w-full mt-2 py-1.5 text-xs rounded-md text-white font-semibold bg-linear-to-r from-indigo-500 to-blue-500 hover:shadow transition"
        >
          Login
        </button>

        <div className="text-center text-[10px] text-gray-500 mt-3">
          New user?{" "}
          <Link
            href="/register"
            className="text-blue-600 hover:underline"
          >
            Create account
          </Link>
        </div>
      </form>
    </div>
  );
}
