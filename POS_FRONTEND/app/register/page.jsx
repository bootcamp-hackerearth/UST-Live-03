"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "@/config/axiosConfig";

export default function RegisterPage() {
  const router = useRouter();

  const [roles, setRoles] = useState([]);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const [form, setForm] = useState({
    name: "",
    username: "",
    phoneNo: "",
    password: "",
    roles: []
  });

  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState("");

  useEffect(() => {
    loadRoles();
  }, []);

  const loadRoles = async () => {
    try {
      const res = await axios.post("/role/list", {
        page: 0,
        sizePerPage: 100
      });

      setRoles(res.data?.content || []);
    } catch (err) {
      console.log("Role Load Error:", err);
    }
  };

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  };

  const handleRoleChange = (e) => {
    setForm({
      ...form,
      roles: e.target.value ? [e.target.value] : []
    });
  };

  const validate = () => {
    const temp = {};

    if (!form.name.trim()) temp.name = "Full Name is required";

    if (!form.username?.trim()) {
      temp.username = "Email is required";
    } else if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.username)) {
      temp.username = "Invalid email format";
    }

    if (!form.phoneNo.trim()) {
      temp.phoneNo = "Phone Number is required";
    } else if (!/^\d{10}$/.test(form.phoneNo)) {
      temp.phoneNo = "Phone Number must be 10 digits";
    }

    if (!form.roles.length) temp.roles = "Please select a role";

    const CREDENTIALS_REQUIRED_MESSAGE = "Password is required";
    const CREDENTIALS_RULE_MESSAGE =
      "Password must be at least 8 characters long and include 1 uppercase letter, 1 number, and 1 special character";

    const pwd = String(form?.password ?? "").trim();

    if (!pwd) {
      temp.password = CREDENTIALS_REQUIRED_MESSAGE;
    } else if (
      !/^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*(),.?":{}|<>_-]).{8,}$/.test(pwd)
    ) {
      temp.password = CREDENTIALS_RULE_MESSAGE;
    }
    setErrors(temp);
    return Object.keys(temp).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setServerError("");

    if (!validate()) return;

    try {
      setLoading(true);

      const res = await axios.post("/user/add", form);

      if (res.data?.success === false) {
        setServerError(res.data.message);
        return;
      }

      alert("Registration Successful");
      router.push("/login");

    } catch (err) {
      console.log(err);
      setServerError("Registration Failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 flex">

      <div className="hidden lg:flex w-1/2 bg-slate-900 text-white p-16 flex-col justify-center">

        <div className="max-w-lg">

          <div className="w-16 h-16 rounded-2xl bg-white text-slate-900 flex items-center justify-center text-2xl font-bold mb-8">
            P
          </div>

          <h1 className="text-5xl font-bold leading-tight">
            Join Smart POS
          </h1>

          <p className="mt-6 text-lg text-slate-400">
            Manage products, inventory, billing and users from one system.
          </p>

        </div>
      </div>

      <div className="w-full lg:w-1/2 flex items-center justify-center p-8">

        <div className="w-full max-w-2xl bg-white border rounded-3xl shadow-xl p-10">

          <h2 className="text-4xl font-bold text-slate-800 mb-2">
            Create Account
          </h2>

          <p className="text-slate-500 mb-8">
            Register to access Smart POS system
          </p>

          <form onSubmit={handleSubmit} className="space-y-6">

            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">

              <div>
                <label htmlFor="name" className="text-sm text-slate-600">Full Name</label>
                <input
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  className="w-full mt-1 px-4 py-3 border rounded-xl"
                />
                {errors.name && <p className="text-red-500 text-sm">{errors.name}</p>}
              </div>

              <div>
                <label htmlFor="username" className="text-sm text-slate-600">Username</label>
                <input
                  name="username"
                  value={form.username}
                  onChange={handleChange}
                  className="w-full mt-1 px-4 py-3 border rounded-xl"
                />
                {errors.username && <p className="text-red-500 text-sm">{errors.username}</p>}
              </div>

              <div>
                <label htmlFor="phoneNo" className="text-sm text-slate-600">Phone</label>
                <input
                  name="phoneNo"
                  value={form.phoneNo}
                  onChange={handleChange}
                  className="w-full mt-1 px-4 py-3 border rounded-xl"
                />
                {errors.phoneNo && <p className="text-red-500 text-sm">{errors.phoneNo}</p>}
              </div>

              <div>
                <label htmlFor="roles" className="text-sm text-slate-600">Role</label>
                <select
                  value={form.roles[0] || ""}
                  onChange={handleRoleChange}
                  className="w-full mt-1 px-4 py-3 border rounded-xl"
                >
                  <option value="">Select Role</option>
                  {roles.map((r) => (
                    <option key={r.identifier} value={r.identifier}>
                      {r.identifier}
                    </option>
                  ))}
                </select>
                {errors.roles && <p className="text-red-500 text-sm">{errors.roles}</p>}
              </div>
            </div>

            <div>
              <label htmlFor="password" className="text-sm text-slate-600">Password</label>

              <div className="relative">
                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  name="password"
                  value={form.password}
                  onChange={handleChange}
                  className="w-full mt-1 px-4 py-3 border rounded-xl pr-16"
                />

                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-sm"
                >
                  {showPassword ? "Hide" : "Show"}
                </button>
              </div>

              {errors.password && (
                <p className="text-red-500 text-sm">{errors.password}</p>
              )}
            </div>

            {serverError && (
              <div className="p-3 bg-red-50 border border-red-200 text-red-600 rounded-xl text-sm">
                {serverError}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-slate-900 text-white py-3 rounded-xl hover:bg-black"
            >
              {loading ? "Creating..." : "Create Account"}
            </button>

            <p className="text-center text-sm text-slate-500">
              Already have an account?{" "}
              <button
                type="button"
                onClick={() => router.push("/login")}
                className="text-slate-900 font-semibold cursor-pointer bg-transparent border-0 p-0"
              >
                Login
              </button>
            </p>

          </form>

        </div>
      </div>
    </div>
  );
}