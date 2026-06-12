"use client"

import { redirect } from "next/navigation";
import { useState } from "react";
import { Validation } from "@/components/Validation";
import PropTypes from "prop-types";

export default function RegisterForm({ roles }) {
  RegisterForm.propTypes = {
    roles: PropTypes.array
  };

  const [errors, setErrors] = useState({})
  const [user, setUser] = useState({
    name: "",
    username: "",
    roles: "",
    phoneNo: "",
    password: ""
  });
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    const newErrors = Validation(user, "register");
    setErrors(newErrors);
    if (Object.keys(newErrors).length > 0) return;

    const res = await fetch("/api/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        ...user,
        roles: [user.roles]
      }),
    })
    const body = await res.json();
    if (body.message) {
      setError(body.message);
      setUser({
        name: "",
        username: "",
        roles: "",
        phoneNo: "",
        password: ""
      })
      return;
    }
    if (res.ok) {
      alert("Registration successful");
      redirect("/login")
    }
    setUser({
      name: "",
      username: "",
      roles: "",
      phoneNo: "",
      password: ""
    })
  }

  const handleChange = (e) => {
    setUser({
      ...user,
      [e.target.name]: e.target.value
    })
  }

  return (
    <div className="min-h-screen flex scrollbar-none" style={{ backgroundImage: "url('/pos2.png')" }}>
      <div className="hidden md:flex w-1/2 text-violet-900 flex-col justify-center items-center p-10">
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
      <div className="w-full md:w-1/2 flex justify-center items-center px-5 scrollbar-none">

        <form
          onSubmit={handleSubmit}
          className="bg-white/10 text-violet-100 
              p-2 pb-4 rounded-xl w-full max-w-md flex flex-col gap-8">

          <h2 className="text-2xl pt-4 font-bold text-center text-violet-600">
            Register
          </h2>

          <input
            type="text"
            name="name"
            value={user.name}
            placeholder="Enter name"
            onChange={handleChange}
            className="p-2 border-violet-900 border-2 rounded-xl placeholder: text-violet-800" />
          {errors.name && (
            <p className="text-red-400 text-sm">{errors.name}</p>
          )}

          <input
            type="email"
            name="username"
            placeholder="Enter email"
            value={user.username}
            onChange={handleChange}
            className="p-2 border-violet-900 border-2 rounded-xl placeholder: text-violet-800" />
          {errors.username && (
            <p className="text-red-400 text-sm">{errors.username}</p>
          )}
          <select
            name="roles"
            value={user.roles}
            onChange={handleChange}
            className="p-2 border-violet-900 border-2 rounded-xl placeholder: text-violet-800">
            <option value="" disabled hidden>
              Select Role
            </option>
            {roles?.map((role) => (
              <option key={role.id} value={role.identifier} className="text-black">
                {role.identifier}
              </option>
            ))}
          </select>
          {errors.roles && (
            <p className="text-red-400 text-sm">{errors.roles}</p>
          )}

          <input
            type="tel"
            name="phoneNo"
            value={user.phoneNo}
            placeholder="Enter Phone Number"
            onChange={handleChange}
            className="p-2 border-violet-900 border-2 rounded-xl placeholder: text-violet-800" />
          {errors.phoneNo && (
            <p className="text-red-400 text-sm">{errors.phoneNo}</p>
          )}

          <input
            type="password"
            name="password"
            value={user.password}
            placeholder="Enter password"
            onChange={handleChange}
            className="p-2 border-violet-900 border-2 rounded-xl placeholder: text-violet-800" />
          {errors.passwordMessage && (
            <p className="text-red-400 text-sm">{errors.password}</p>
          )}

          <button
            type="submit"
            className="bg-violet-600 hover:bg-violet-700 transition text-white py-3 rounded-md">
            Submit
          </button>
          <p className="text-violet-400">Already have account <a href='/login' className='text-violet-500'> Login</a></p>
          {error && <div className=" text-red-500 text-center text-sm">{error}</div>}
        </form>
      </div>
    </div>
  )
}