"use client"
import { useForm } from 'react-hook-form'
import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { requiredValidation, emailValidation, nameValidation, passwordValidation, phoneValidation } from "@/validation/validation"
import { AlertCircle } from "lucide-react"

const Register = () => {

  const navigate = useRouter()
  const [serverError, setServerError] = useState("");

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm()

  const onSubmit = async (data) => {
    const res = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    });
    console.log(res)
    const response = await res.json();

    console.log(response);
    console.log(response.success);

    if (response.success) {
      alert("register success, login to your account")
      navigate.push("/login")
      reset()
    } else {
      setServerError(response.message);
    }

  }

  const [roles, setroles] = useState([]);

  const paginationDto = {
    "page": 0,
    "sizePerPage": 100
  }

  async function getRoles(paginationDto) {
    const res = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/role/list`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(paginationDto)
    });

    const response = await res.json();

    setroles(response.dtoList)
  }

  useEffect(() => {
    getRoles(paginationDto)
  }, [])



  return (
    <div className="flex min-h-screen bg-slate-50">
      <div className="hidden lg:flex lg:w-1/2 bg-slate-900 relative items-center justify-center p-12 text-white overflow-hidden">
        <div className="absolute inset-0 bg-[linear-gradient(to_right,#1e293b_1px,transparent_1px),linear-gradient(to_bottom,#1e293b_1px,transparent_1px)] bg-size-[4rem_4rem] mask-[radial-gradient(ellipse_60%_50%_at_50%_0%,#000_70%,transparent_100%)] opacity-60" />
        <div className="absolute inset-0 bg-linear-to-b from-slate-900 via-slate-900 to-slate-950 opacity-90" />

        <div className="relative z-10 max-w-lg w-full">


          <h1 className="text-4xl font-extrabold tracking-tight leading-none mb-4 bg-linear-to-br from-white via-slate-100 to-slate-400 bg-clip-text text-transparent">
            Point of Sales Application
          </h1>
          <p className="text-slate-400 text-base leading-relaxed mb-8">
            New User Registration
          </p>

          <div className="space-y-4 border-t border-slate-800 pt-8">
            <div className="flex items-start gap-3">
              <div className="mt-1 flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 text-xs">
                ✓
              </div>
              <div>
                <p className="font-semibold text-sm text-slate-200">Authentication</p>
              </div>
            </div>
            <div className="flex items-start gap-3">
              <div className="mt-1 flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 text-xs">
                ✓
              </div>
              <div>
                <p className="font-semibold text-sm text-slate-200">Role based Authorization</p>

              </div>
            </div>
          </div>
        </div>

        <div className="absolute bottom-6 left-12 text-xs text-slate-500 font-medium">
          © 2026 All rights reserved.
        </div>
      </div>

      <div className="w-full lg:w-1/2 flex justify-center items-center p-4 sm:p-8 md:p-12">
        <div className="w-full max-w-md bg-white border border-slate-200/80 shadow-sm rounded-2xl p-8">

          <div className="mb-6 text-center">
            <h2 className="text-2xl font-bold text-slate-900 tracking-tight">
              Register User
            </h2>
            <p className="text-xs text-slate-500 mt-1">Create a new user profile below.</p>
          </div>

          {serverError && (
            <div className="flex items-center gap-2 p-3 mb-4 rounded-lg text-sm font-medium bg-rose-50 border border-rose-200 text-rose-800 transition-all">
              <AlertCircle className="w-4 h-4 shrink-0 text-rose-600" />
              <p>{serverError}</p>
            </div>
          )}

          <form
            onSubmit={handleSubmit(onSubmit)}
            className="space-y-4"
          >
            <div className="flex flex-col">
              <label htmlFor="username" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">
                Username / Email
              </label>
              <input
                id="username"
                type="text"
                {...register("username", emailValidation)}
                placeholder="name@example.com"
                className={`h-10 px-3.5 border rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-900/10 transition-all text-sm font-medium text-slate-800 placeholder:text-slate-400 bg-white
                                    ${errors.username
                    ? "border-rose-400 bg-rose-50/10 focus:border-rose-500"
                    : "border-slate-200 focus:border-slate-900"
                  }`}
              />
              {errors.username && (
                <p className="text-rose-600 text-xs mt-1.5 font-medium flex items-center gap-1">
                  <span>⚠</span> {errors.username.message}
                </p>
              )}
            </div>

            <div className="flex flex-col">
              <label htmlFor="name" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">
                Full Name
              </label>
              <input
                id="name"
                type="text"
                {...register("name", nameValidation)}
                placeholder="John Doe"
                className={`h-10 px-3.5 border rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-900/10 transition-all text-sm font-medium text-slate-800 placeholder:text-slate-400 bg-white
                                    ${errors.name
                    ? "border-rose-400 bg-rose-50/10 focus:border-rose-500"
                    : "border-slate-200 focus:border-slate-900"
                  }`}
              />
              {errors.name && (
                <p className="text-rose-600 text-xs mt-1.5 font-medium flex items-center gap-1">
                  <span>⚠</span> {errors.name.message}
                </p>
              )}
            </div>

            <div className="flex flex-col">
              <label htmlFor="phoneNo" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">
                Phone Number
              </label>
              <input
                id="phoneNo"
                type="tel"
                {...register("phoneNo", phoneValidation)}
                placeholder="000-000-0000"
                className={`h-10 px-3.5 border rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-900/10 transition-all text-sm font-medium text-slate-800 placeholder:text-slate-400 bg-white
                                    ${errors.phoneNo
                    ? "border-rose-400 bg-rose-50/10 focus:border-rose-500"
                    : "border-slate-200 focus:border-slate-900"
                  }`}
              />
              {errors.phoneNo && (
                <p className="text-rose-600 text-xs mt-1.5 font-medium flex items-center gap-1">
                  <span>⚠</span> {errors.phoneNo.message}
                </p>
              )}
            </div>

            <div className="flex flex-col">
              <label htmlFor="roles" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">
                Select Roles
              </label>
              <select
                id="roles"
                {...register("roles", requiredValidation)}
                multiple
                className={`text-sm px-3.5 py-2 border rounded-lg bg-white font-medium text-slate-800 h-28 focus:outline-none focus:ring-2 focus:ring-slate-900/10 transition-all cursor-pointer p-2
                                    ${errors.roles
                    ? "border-rose-400 bg-rose-50/10 focus:border-rose-500"
                    : "border-slate-200 focus:border-slate-900"
                  }`}
              >
                {roles?.map((role) => (
                  <option key={role.identifier} value={role.identifier} className="py-1 px-1 text-slate-700 text-sm">
                    {role.identifier}
                  </option>
                ))}
              </select>
              <p className="text-[11px] text-slate-400 mt-1.5 leading-relaxed bg-slate-50 p-2 border border-slate-100 rounded-md">
                Hold <kbd className="font-sans font-semibold text-slate-700">Ctrl</kbd> (Windows) or <kbd className="font-sans font-semibold text-slate-700">⌘ Cmd</kbd> (Mac) to switch multiple tags.
              </p>
              {errors.roles && (
                <p className="text-rose-600 text-xs mt-1.5 font-medium flex items-center gap-1">
                  <span>⚠</span> {errors.roles.message}
                </p>
              )}
            </div>

            <div className="flex flex-col">
              <label htmlFor="password" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">
                Password
              </label>
              <input
                id="password"
                type="password"
                {...register("password", passwordValidation)}
                placeholder="••••••••"
                className={`h-10 px-3.5 border rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-900/10 transition-all text-sm font-medium text-slate-800 placeholder:text-slate-400 bg-white
                                    ${errors.password
                    ? "border-rose-400 bg-rose-50/10 focus:border-rose-500"
                    : "border-slate-200 focus:border-slate-900"
                  }`}
              />
              {errors.password && (
                <p className="text-rose-600 text-xs mt-1.5 font-medium flex items-center gap-1">
                  <span>⚠</span> {errors.password.message}
                </p>
              )}
            </div>

            <button
              type="submit"
              className="w-full bg-slate-900 text-white py-2.5 rounded-lg hover:bg-slate-800 font-semibold text-sm shadow-sm transition-colors mt-6 cursor-pointer"
            >
              Register Account
            </button>

            <button
              type="button"
              onClick={() => navigate.push("/login")}
              className="w-full text-center text-xs font-semibold text-slate-500 hover:text-slate-900 cursor-pointer mt-4 transition-colors"
            >
              Have an account? <span className="underline decoration-slate-300 underline-offset-4">Login</span>
            </button>

          </form>
        </div>
      </div>
    </div>
  )
}

export default Register