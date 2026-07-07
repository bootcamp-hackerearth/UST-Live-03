"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import axios from "axios";

export default function Login() {
  const router = useRouter();
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors: formErrors },
  } = useForm({
    defaultValues: {
      username: "",
      password: "",
    },
  });

  const onSubmit = async (data) => {
    setLoading(true);
    setError("");

    try {
      const res = await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/api/authenticate`,
        {
          username: data.username,
          password: data.password,
        }
      );

      if (
        !res.data?.token ||
        res.data.token === "Error" ||
        res.data.message === "Error" ||
        res.data.username == null
      ) {
        setError("Invalid username or password");
        setLoading(false);
        return;
      }

      console.log("Login Response:", res.data);

      if (!res.data?.token) {
        setError("Invalid username or password");
        setLoading(false);
        return;
      }

      localStorage.setItem("token", res.data.token);
      localStorage.setItem("name", res.data.name || "");
      localStorage.setItem("username", res.data.username || data.username);
      localStorage.setItem("phoneNo", res.data.phoneNo || "");
      localStorage.setItem("roles", JSON.stringify(res.data.roles || []));

      router.push("/home");
    } catch (err) {
      console.log(err);

      if (err?.response?.status === 401) {
        setError("Invalid username or password");
      } else if (err?.response?.status === 403) {
        setError("Access denied");
      } else {
        setError("User not found or login failed");
      }
    } finally {
      setLoading(false);
    }
  };
  const hasError = !!error || !!formErrors.username || !!formErrors.password;
  const displayedError =
    formErrors.username?.message || formErrors.password?.message || error;

  return (
    <div className="min-h-screen bg-[#F4F4F4] overflow-hidden flex items-center justify-center relative">
      <div className="absolute left-0 top-0 w-[40%] h-full bg-black rounded-r-[90px]"></div>
      <div className="absolute bottom-[-80px] left-[14%] w-[240px] h-[240px] bg-white rounded-full"></div>

      <div className="relative z-10 w-full max-w-6xl grid lg:grid-cols-2 items-center px-8">
        <div className="text-white pl-8">
          <h1 className="text-5xl font-bold leading-[1.1] tracking-[-2px]">
            Smart POS
            <br />
            Management
          </h1>
          <p className="text-lg text-gray-300 mt-6 leading-8 max-w-sm">
            Billing, inventory and retail operations made simple.
          </p>
        </div>

        <div className="relative flex justify-center items-center h-[560px]">
          <div className="relative z-10">
            <div className="w-[350px] bg-white rounded-[30px] shadow-[0_15px_60px_rgba(0,0,0,0.16)] p-8">
              <h2 className="text-5xl font-bold text-black mb-8">Login</h2>

              <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                <input
                  type="text"
                  placeholder="Enter Email"
                  {...register("username", {
                    required: "Please enter email",
                    pattern: {
                      value: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
                      message: "Please enter a valid email address",
                    },
                  })}
                  className={`w-full h-12 rounded-xl px-4 text-sm text-black placeholder:text-black outline-none focus:border-black ${hasError ? "border border-red-500" : "border border-gray-300"
                    }`}
                />

                <input
                  type="password"
                  placeholder="Enter Password"
                  {...register("password", {
                    required: "Please enter password",
                  })}
                  className={`w-full h-12 rounded-xl px-4 text-sm text-black placeholder:text-black outline-none focus:border-black ${hasError ? "border border-red-500" : "border border-gray-300"
                    }`}
                />

                {displayedError && (
                  <div className="text-red-500 text-sm">{displayedError}</div>
                )}

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full h-12 rounded-xl bg-black text-white text-base font-semibold hover:bg-[#1A1A1A] disabled:opacity-70"
                >
                  {loading ? "Signing In..." : "Login"}
                </button>
              </form>

              <div className="mt-6 text-center text-gray-500 text-sm">
                Don’t have an account?{" "}
                <button
                  type="button"
                  onClick={() => router.push("/register")}
                  className="text-black font-semibold hover:underline"
                >
                  Sign Up
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}