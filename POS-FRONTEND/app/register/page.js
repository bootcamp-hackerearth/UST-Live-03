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
        "/api/role/list",
        {
          page: 0,
          sizePerPage: 50,
          sortDirection: "ASC",
          sortField: "identifier"
        }
      );
   
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

    if (!formData.name.trim()) err.name = "Name required";

    const email = formData.username.trim();

    if (!email) {
      err.username = "Email required";
    } else if (email.length > 254) {
      err.username = "Email is too long";
    } else {
      const parts = email.split("@");
      
      if (parts.length === 2) {
        const [localPart, domainPart] = parts;
        const hasDot = domainPart.includes(".");
        const domainSegments = domainPart.split(".");
        
        const isValidStructure = 
          localPart.length > 0 && 
          hasDot && 
          domainSegments.every(seg => seg.length > 0) &&
          !/\s/.test(email); 

        if (!isValidStructure) {
          err.username = "Invalid email";
        }
      }
    }

    if (!/^\d{10}$/.test(formData.phoneNo)) {
      err.phoneNo = "10 digits required";
    }

    if (formData.roles.length === 0) {
      err.roles = "Select at least one role";
    }
    if (formData.password.length < 6) {
      err["password"] = "Min "+"6 characters";
    }

    if (formData.password !== formData.confirmPassword) {
      err["confirmPassword"] = "Pass"+"words do not match";
    }

    setErrors(err);
    return Object.keys(err).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) return;

    try {
      await axios.post("/api/user/register", {
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
    <div className="min-h-screen flex items-center justify-center bg-gray-100 px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-lg border border-gray-200">
        <div className="bg-black text-white text-center p-6 rounded-t-2xl">
          <h2 className="text-xl font-bold">User Registration</h2>
          <p className="text-gray-300 text-sm mt-1">
            Create your account
          </p>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <Input
            name="name"
            placeholder="Full Name"
            value={formData.name}
            onChange={handleChange}
            error={errors.name}
          />

          <Input
            name="username"
            placeholder="Email Address"
            value={formData.username}
            onChange={handleChange}
            error={errors.username}
          />

          <Input
            name="phoneNo"
            placeholder="Phone Number"
            value={formData.phoneNo}
            onChange={handleChange}
            error={errors.phoneNo}
          />

          <Input
            name="password"
            type="password"
            placeholder="Password"
            value={formData.password}
            onChange={handleChange}
            error={errors.password}
          />

          <Input
            name="confirmPassword"
            type="password"
            placeholder="Confirm Password"
            value={formData.confirmPassword}
            onChange={handleChange}
            error={errors.confirmPassword}
          />

          <div>
            <p className="text-sm font-semibold text-gray-700 mb-2">
              Select Roles
            </p>

            <div className="flex flex-wrap gap-2">
              {roles.map((role) => {
                const active = formData.roles.includes(role.identifier);
                const key = role.identifier || role.id; 

                return (
                  <label
                    key={key}
                    className={`
                      px-3 py-1.5 text-sm rounded-lg border cursor-pointer transition
                      ${
                        active
                          ? "bg-black text-white border-black"
                          : "bg-white text-gray-700 border-gray-300 hover:bg-gray-100"
                      }
                    `}
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
              <p className="text-red-500 text-xs mt-1">
                {errors.roles}
              </p>
            )}
          </div>

          <button
            type="submit"
            className="w-full bg-black text-white py-2.5 rounded-lg font-medium hover:bg-gray-800 transition"
          >
            Register
          </button>

          <button
            type="button"
            onClick={() => router.push("/login")}
            className="w-full border border-gray-300 py-2.5 rounded-lg text-sm text-gray-700 hover:bg-gray-50 transition"
          >
            Already have an account? Login
          </button>
        </form>
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
    <div className="space-y-1">
      <input
        name={name}
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm bg-white text-gray-800 placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-black"
      />

      {error && (
        <p className="text-red-500 text-xs font-medium">
          {error}
        </p>
      )}
    </div>
  );
}

Input.propTypes = {
  name: PropTypes.string.isRequired,
  placeholder: PropTypes.string,
  value: PropTypes.any,
  onChange: PropTypes.func,
  error: PropTypes.string,
  type: PropTypes.string,
};
