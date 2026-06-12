"use client"
import { useForm } from "react-hook-form"
import { useRouter } from "next/navigation";
import React from 'react'
import { ShoppingBag, KeyRound, User, Store } from "lucide-react"

const Login = () => {
    const router = useRouter()

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm()

    const onSubmit = async (data) => {
        debugger
        const res = await fetch("/api/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });
        
        console.log(res);

        if (res.ok) {
            router.push("/");
            localStorage.setItem("username", data.username);
        } else {
            alert("Invalid username or password");
        }
    }

    return (
        <div className="flex min-h-screen bg-slate-50">

            <div className="w-full lg:w-[45%] flex flex-col justify-center items-center p-8 sm:p-12 md:p-16 bg-white z-10 shadow-sm">
                <div className="w-full max-w-sm">

                    <div className="flex items-center gap-2 mb-8">
                        <div className="p-2 bg-slate-900 rounded-xl text-white">
                            <Store className="w-6 h-6" />
                        </div>
                        <span className="text-xl font-bold tracking-tight text-slate-900">POS</span>
                    </div>

                    <div className="mb-6">
                        <h2 className="text-2xl font-bold text-slate-900 tracking-tight">
                            Welcome Back
                        </h2>
                        <p className="text-xs text-slate-500 mt-1">Please enter the credentials</p>
                    </div>

                    <form
                        onSubmit={handleSubmit(onSubmit)}
                        className="space-y-4"
                    >
                        <div className="flex flex-col">
                            <label htmlFor="username" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">
                                Username / Email
                            </label>
                            <div className="relative">
                                <span className="absolute inset-y-0 left-0 flex items-center pl-3.5 pointer-events-none text-slate-400">
                                    <User className="w-4 h-4" />
                                </span>
                                <input
                                    id="username"
                                    {...register("username", { required: { value: true, message: "Username is required" } })}
                                    type="email"
                                    placeholder="name@example.com"
                                    className={`w-full h-10 pl-10 pr-3.5 border rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-900/10 transition-all text-sm font-medium text-slate-800 placeholder:text-slate-400 bg-white
                                        ${errors.username
                                            ? "border-rose-400 bg-rose-50/10 focus:border-rose-500"
                                            : "border-slate-200 focus:border-slate-900"
                                        }`}
                                />
                            </div>
                            {errors.username && (
                                <p className="text-rose-600 text-xs mt-1.5 font-medium flex items-center gap-1">
                                    <span>⚠</span> {errors.username.message}
                                </p>
                            )}
                        </div>

                        <div className="flex flex-col">
                            <label htmlFor="password" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">
                                Password
                            </label>
                            <div className="relative">
                                <span className="absolute inset-y-0 left-0 flex items-center pl-3.5 pointer-events-none text-slate-400">
                                    <KeyRound className="w-4 h-4" />
                                </span>
                                <input
                                    id="password"
                                    {...register("password", { required: { value: true, message: "Password is required" } })}
                                    type="password"
                                    placeholder="••••••••"
                                    className={`w-full h-10 pl-10 pr-3.5 border rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-900/10 transition-all text-sm font-medium text-slate-800 placeholder:text-slate-400 bg-white
                                        ${errors.password
                                            ? "border-rose-400 bg-rose-50/10 focus:border-rose-500"
                                            : "border-slate-200 focus:border-slate-900"
                                        }`}
                                />
                            </div>
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
                            Login
                        </button>

                        <button
                            type="button"
                            onClick={() => router.push("/register")}
                            className="w-full text-center text-xs font-semibold text-slate-500 hover:text-slate-900 cursor-pointer mt-4 transition-colors"
                        >
                            Don't have an account? <span className="underline decoration-slate-300 underline-offset-4">Register</span>
                        </button>

                    </form>
                </div>
            </div>

            <div className="hidden lg:flex lg:w-[55%] bg-slate-950 relative flex-col justify-between p-16 overflow-hidden">

                <div className="absolute top-0 right-0 w-125 h-125 bg-blue-600/10 rounded-full blur-3xl -mr-40 -mt-40"></div>
                <div className="absolute bottom-0 left-0 w-100 h-100 bg-emerald-600/5 rounded-full blur-3xl -ml-20 -mb-20"></div>

                <div className="relative z-10 flex items-center justify-between w-full">
                    <span className="text-xs font-semibold tracking-widest text-slate-400 uppercase">Secure Environment</span>
                    <span className="flex items-center gap-1.5 text-xs text-emerald-400 font-medium bg-emerald-500/10 px-2.5 py-1 rounded-full border border-emerald-500/20">
                        <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span> UST
                    </span>
                </div>

                <div className="relative z-10 max-w-md my-auto">
                    <div className="w-12 h-12 bg-white/5 border border-white/10 rounded-2xl flex items-center justify-center text-white mb-6 backdrop-blur-sm">
                        <ShoppingBag className="w-5 h-5 text-slate-300" />
                    </div>
                    <h1 className="text-4xl font-bold text-white tracking-tight leading-tight">
                        Retail POS Application
                    </h1>
                </div>

                <div className="relative z-10 flex items-center justify-between pt-6 border-t border-white/5 text-xs text-slate-500 font-medium">
                    <p>© 2026 POS Systems Inc.</p>
                </div>

            </div>

        </div>
    )
}

export default Login