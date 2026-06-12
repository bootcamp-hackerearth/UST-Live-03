"use client";

import React, { useEffect, useState } from "react";
import axios from "axios";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";

export default function Register() {
  const router = useRouter();

  const [roles, setRoles] = useState([]);
  const [errors, setErrors] = useState({});

  const [formData, setFormData] = useState({
    name: "",
    username: "",
    phoneNo: "",
    password: "",
    confirmPassword: "",
    roles: [],
  });

  useEffect(() => {
    fetchRoles();
  }, []);

 const fetchRoles = async () => {
  try {
    const res = await axios.post(
      "http://localhost:8080/api/role/list",
      {
        page: 0,
        sizePerPage: 50,
        sortDirection: "ASC",
        sortField: "identifier"
      }
    );

    console.log("ROLES RESPONSE ", res.data);

    setRoles(res.data?.dtoList || []);
  } catch (error) {
    console.error("ROLE FETCH ERROR ", error);
    setRoles([]);
  }
};

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "phoneNo") {
      if (!/^\d*$/.test(value)) return;
      if (value.length > 10) return;
    }

    setFormData((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const handleRoleChange = (e) => {
    const { value, checked } = e.target;

    setFormData((prev) => ({
      ...prev,
      roles: checked
        ? [...prev.roles, value]
        : prev.roles.filter((r) => r !== value),
    }));

    setErrors((prev) => ({ ...prev, roles: "" }));
  };

  const validateForm = () => {
  let err = {};

  if (!formData.name.trim()) {
    err.name = "Name required";
  }

  const email = formData.username;
  const isValidEmail =
    email &&
    email.includes("@") &&
    email.includes(".") &&
    email.indexOf("@") < email.lastIndexOf(".");

  if (!isValidEmail) {
    err.username = "Invalid email";
  }

  if (!/^\d{10}$/.test(formData.phoneNo)) {
    err.phoneNo = "10 digits required";
  }

  if (formData.roles.length === 0) {
    err.roles = "Select at least one role";
  }

  if (formData.password.length < 6) {
    err.password = "Min 6 characters";
  }

  if (formData.password !== formData.confirmPassword) {
    err.confirmPassword = "Passwords do not match";
  }

  setErrors(err);
  return Object.keys(err).length === 0;
};


  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) return;

    try {
      await axios.post("http://localhost:8080/api/user/register", {
        username: formData.username,
        name: formData.name,
        phoneNo: formData.phoneNo,
        password: formData.password,
        roles: formData.roles,
      });

      alert("Registered Successfully ");
      router.push("/login");
    } catch (err) {
      alert(err.response?.data?.message || "Registration failed ");
    }
  };

  return (
    <div className="min-h-screen flex">

      {/* LEFT SIDE (same as login) */}
      <div className="hidden md:flex w-1/2 bg-gradient-to-br from-[#020617] via-[#020c2f] to-[#0a1f66] text-white flex-col justify-center px-16">
        <h1 className="text-4xl font-bold">Join Us </h1>
        <p className="mt-4 text-blue-200 text-lg">
          Create an account and get started instantly.
        </p>

        <div className="mt-10 space-y-3 text-blue-300 text-sm">
          <p> Fast onboarding</p>
          <p> Secure platform</p>
          <p> Role-based access</p>
        </div>
      </div>

      <div className="w-full md:w-1/2 flex items-center justify-center bg-white px-6">

        <div className="w-full max-w-md bg-gradient-to-br from-[#020617] via-[#020c2f] to-[#0a1f66] border border-white/10 shadow-2xl rounded-2xl text-white">

          <div className="text-center p-6 border-b border-white/10">
            <h2 className="text-2xl font-bold">Register</h2>
            <p className="text-blue-200 text-sm mt-1">
              Create your account
            </p>
          </div>

          <form onSubmit={handleSubmit} className="p-6 space-y-4">

            <Input name="name" placeholder="Full Name" value={formData.name} onChange={handleChange} error={errors.name} />
            <Input name="username" placeholder="Email Address" value={formData.username} onChange={handleChange} error={errors.username} />
            <Input name="phoneNo" placeholder="Phone Number" value={formData.phoneNo} onChange={handleChange} error={errors.phoneNo} />

            <Input name="password" type="password" placeholder="Password" value={formData.password} onChange={handleChange} error={errors.password} />
            <Input name="confirmPassword" type="password" placeholder="Confirm Password" value={formData.confirmPassword} onChange={handleChange} error={errors.confirmPassword} />

            <div>
              <p className="text-xs text-blue-200 mb-2 uppercase">
                Select Roles
              </p>

              <div className="flex flex-wrap gap-2">
                {roles.map((role) => {
                  const active = formData.roles.includes(role.identifier);

                  return (
                    <label
                      key={role.identifier || role.id}
                      className={`px-3 py-1.5 text-sm rounded-lg cursor-pointer transition border
                        ${
                          active
                            ? "bg-white text-blue-900 border-white"
                            : "bg-black/20 text-blue-200 border-white/20 hover:bg-black/30"
                        }`}
                    >
                      <input
                        type="checkbox"
                        value={role.identifier}
                        checked={active}
                        onChange={handleRoleChange}
                        className="hidden"
                      />
                      {role.identifier}
                    </label>
                  );
                })}
              </div>

              {errors.roles && (
                <p className="text-red-300 text-xs mt-1">
                  {errors.roles}
                </p>
              )}
            </div>

            <button
              type="submit"
              className="w-full py-2.5 rounded-lg bg-white text-blue-900 font-semibold hover:bg-blue-100 transition"
            >
              Register
            </button>

            <button
              type="button"
              onClick={() => router.push("/login")}
              className="w-full border border-white/20 py-2.5 rounded-lg text-sm text-blue-200 hover:bg-white/10 transition"
            >
              Already have an account? Login
            </button>

          </form>
        </div>
      </div>
    </div>
  );
}


function Input({
  name,
  placeholder,
  value,
  onChange,
  error,
  type = "text",
}) {
  return (
    <div>
      <input
        name={name}
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        className="w-full px-3 py-2 rounded-lg bg-black/20 border border-white/20 text-white placeholder:text-blue-300 focus:outline-none focus:ring-2 focus:ring-blue-300"
      />
      {error && (
        <p className="text-red-300 text-xs mt-1">{error}</p>
      )}
    </div>
  );
}

Input.propTypes = {
  name: PropTypes.string.isRequired,
  placeholder: PropTypes.string,
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
  error: PropTypes.string,
  type: PropTypes.string,
};


