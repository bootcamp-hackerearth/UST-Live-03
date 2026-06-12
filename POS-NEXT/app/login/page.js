"use client"

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function Login() {

  const router = useRouter();
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [credentials, setCredentials] = useState({
    username: "",
    password: "",
  });

  const handleChange = (e) => {
    setCredentials({
      ...credentials,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    console.log("Submitting login...");
    const res = await fetch("/api/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(credentials),
    });

    if (res.ok) {
      router.push("/");
      localStorage.setItem("username", credentials.username);
    } else {
      setError("Invalid username or password");
    }

    setLoading(false);
  };

  return (
    <div className="min-h-screen flex bg-linear-to-r from-white to-violet-200" style={{ backgroundImage: "url('/pos2.png')" }}>
      <div className="hidden md:flex w-1/2 text-violet-900 flex-col justify-center items-center p-10"
      >
        <h1 className="text-4xl font-bold mb-4">
          POS System
        </h1>

        <p className="text-lg text-center max-w-md text-violet-950">
          Smart and efficient point-of-sale solution designed to simplify your business operations, manage inventory, and track sales seamlessly.
        </p>

        <div className="mt-8 text-sm text-violet-900 text-center">
          Fast • Secure • Reliable
        </div>

      </div>
      <div className="w-full md:w-1/2 flex justify-center items-center px-6">
        <form onSubmit={handleSubmit} className=" backdrop-blur-xl border border-white/20
        p-8 rounded-xl w-full max-w-md flex flex-col gap-8">

          <h2 className="text-2xl font-bold text-center text-violet-900">
            Login
          </h2>
          <input
            name="username"
            type="text"
            placeholder="Username"
            value={credentials.username}
            onChange={handleChange}
            className="p-2 border-violet-900 border-2 rounded-xl placeholder: text-violet-800"

          />

          <input
            type="password"
            name="password"
            placeholder="Password"
            value={credentials.password}
            onChange={handleChange}
            className="p-2 border-violet-900 border-2 rounded-xl placeholder: text-violet-800"/>
            {error && <p className="text-red-500">{error}</p>}

          <button className="bg-violet-600 text-white p-2">
            {loading ? "Logging in..." : "Login"}
          </button>
          <p className="text-violet-400">Dont have an account <a href="/register" className='text-violet-600'> Register</a></p>

        </form>
      </div>
    </div>
  );
}