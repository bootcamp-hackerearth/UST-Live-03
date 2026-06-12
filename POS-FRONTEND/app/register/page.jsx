"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axiosInstance from "../api/axiosInstance";

function Register() {
  const router = useRouter();

  const [form, setForm] = useState({
    username: "",
    name: "",
    phoneNo: "",
    roles: [],
    password: "",
  });

  const [rolesList, setRolesList] = useState([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchRoles = async () => {
      const endpoints = ["/role/findByStatus", "/role/findAllActive"];
      let foundRoles = [];

      for (const endpoint of endpoints) {
        try {
          const response = await axiosInstance.get(endpoint);
          const data = response.data;

          const roles = Array.isArray(data)
            ? data
            : data?.dtoList || data?.content || data?.data || [];

          if (roles.length > 0) {
            foundRoles = roles;
            break;
          }
        } catch (err) {
          if (err.response?.status !== 404) {
            console.error(`ROLE FETCH ERROR (${endpoint}):`, err);
          }
        }
      }

      if (foundRoles.length > 0) {
        setRolesList(foundRoles);
      } else {
        setMessage("Failed to load roles. Please try again later.");
      }
    };

    fetchRoles();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleRoleChange = (roleIdentifier) => {
    const updatedRoles = form.roles.includes(roleIdentifier)
      ? form.roles.filter(
          (selectedRole) => selectedRole !== roleIdentifier
        )
      : [...form.roles, roleIdentifier];

    setForm({ ...form, roles: updatedRoles });
  };

  const getRoleIdentifier = (role) =>
    typeof role === "string"
      ? role
      : role?.identifier || role?.name || role?.id || "";

  const handleRegister = async (e) => {
    e.preventDefault();
    setMessage("");

    if (!form.username || !form.password) {
      setMessage("Username and password required");
      return;
    }

    if (form.roles.length === 0) {
      setMessage("Please select at least one role");
      return;
    }

    setLoading(true);

    try {
      const response = await axiosInstance.post("/user/add", {
        username: form.username,
        name: form.name,
        password: form.password,
        phoneNo: form.phoneNo,
        roles: form.roles,
      });

      const result = response.data || {};
      const serverMessage = (result.message || "")
        .toString()
        .toLowerCase();

      if (
        /already exists|username already exists|user already exists/.test(
          serverMessage
        )
      ) {
        setMessage("Username already exists");
        return;
      }

      setMessage("User registered successfully");
      setTimeout(() => router.push("/login"), 1000);
    } catch (err) {
      console.error(err);

      if (
        err.response?.status === 409 ||
        err.response?.data?.message ===
          "Username already exists"
      ) {
        setMessage("Username already exists");
      } else if (err.response?.status) {
        setMessage("Failed: " + err.response.status);
      } else {
        setMessage("Server error");
      }
    } finally {
      setLoading(false);
    }
  };

  const isSuccess =
    message.toLowerCase().includes("successfully");

  const inputClass =
    "w-full rounded-lg border border-slate-200 bg-white px-4 py-2.5 text-sm font-medium text-slate-900 placeholder:text-slate-400 outline-none transition focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100";

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-100 px-4">
      <div className="w-full max-w-2xl rounded-2xl bg-white p-8 shadow-xl">
        <div className="mb-6 text-center">
          <h1 className="text-3xl font-bold text-cyan-600">
            POS
          </h1>
          <h2 className="mt-2 text-2xl font-bold text-slate-800">
            Create user
          </h2>
          <p className="mt-2 text-sm text-slate-500">
            Add a new team member and assign access roles.
          </p>
        </div>

        {message && (
          <div
            className={`mb-5 rounded-lg border px-4 py-3 text-sm font-medium ${
              isSuccess
                ? "border-emerald-100 bg-emerald-50 text-emerald-700"
                : "border-rose-100 bg-rose-50 text-rose-700"
            }`}
          >
            {message}
          </div>
        )}

        <form
          onSubmit={handleRegister}
          className="space-y-5"
        >
          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label
                htmlFor="name"
                className="mb-1.5 block text-sm font-bold text-slate-700"
              >
                Full Name
              </label>
              <input
                id="name"
                name="name"
                type="text"
                className={inputClass}
                placeholder="Enter full name"
                value={form.name}
                onChange={handleChange}
              />
            </div>

            <div>
              <label
                htmlFor="username"
                className="mb-1.5 block text-sm font-bold text-slate-700"
              >
                Username
              </label>
             <input
             id="username"
             name="username"
             type="email"
            className={inputClass}
            placeholder="Enter email address"
            value={form.username}
            onChange={handleChange}
            pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"
            title="Please enter a valid email address"
             />
            </div>

            <div>
              <label
                htmlFor="phoneNo"
                className="mb-1.5 block text-sm font-bold text-slate-700"
              >
                Phone Number
              </label>
         <input
         id="phoneNo"
         name="phoneNo"
         type="text"
         className={inputClass}
         placeholder="Enter phone number"
         value={form.phoneNo}
         onChange={handleChange}
         maxLength={10}
         pattern="[0-9]{10}"
         title="Phone Number must contain exactly 10 digits"
        />
            </div>

            <div>
              <label
                htmlFor="password"
                className="mb-1.5 block text-sm font-bold text-slate-700"
              >
                Password
              </label>
             <input
             id="password"
            name="password"
            type="password"
           className={inputClass}
           placeholder="Enter password"
          value={form.password}
          onChange={handleChange}
          minLength={6}
          title="Password must be at least 6 characters long"
         />
            </div>
          </div>

          <div>
            <label
              htmlFor="roles"
              className="mb-1.5 block text-sm font-bold text-slate-700"
            >
              Roles
            </label>

            <div
              id="roles"
              className="rounded-lg border border-slate-200 bg-slate-50 p-3"
            >
              {rolesList.length > 0 ? (
                <div className="flex flex-wrap gap-3">
                  {rolesList.map((role) => {
                    const roleIdentifier =
                      getRoleIdentifier(role);

                    return (
                      <label
                        key={roleIdentifier}
                        className="flex items-center gap-2 rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm font-semibold text-slate-700 shadow-sm"
                      >
                        <input
                          type="checkbox"
                          checked={form.roles.includes(
                            roleIdentifier
                          )}
                          onChange={() =>
                            handleRoleChange(
                              roleIdentifier
                            )
                          }
                          className="h-4 w-4 rounded border-slate-300 text-cyan-600 focus:ring-cyan-500"
                        />
                        {roleIdentifier}
                      </label>
                    );
                  })}
                </div>
              ) : (
                <p className="text-sm font-medium text-slate-500">
                  No roles available
                </p>
              )}
            </div>
          </div>

          <button
            type="submit"
            disabled={
              !form.username ||
              !form.password ||
              loading
            }
            className="w-full rounded-lg bg-cyan-600 px-4 py-2.5 text-sm font-bold text-white shadow-lg shadow-cyan-200/70 transition hover:bg-cyan-700 disabled:cursor-not-allowed disabled:bg-cyan-300 disabled:shadow-none"
          >
            {loading
              ? "Registering..."
              : "Register User"}
          </button>
        </form>

        <button
          type="button"
          onClick={() => router.push("/login")}
          className="mt-4 w-full rounded-lg border border-slate-200 bg-white px-4 py-2.5 text-sm font-bold text-slate-700 transition hover:bg-slate-50"
        >
          Back to Login
        </button>
      </div>
    </div>
  );
}

export default Register;