"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import Link from "next/link";
import { useRouter } from "next/navigation";

export default function Register() {
  const router = useRouter();

  const [roles, setRoles] = useState([]);
  const [editMode, setEditMode] = useState(false);
  const [errors, setErrors] = useState({});
  const VALIDATION_MESSAGES = {
    CREDENTIALS_REQUIRED: "Password is required",
    CREDENTIALS_INVALID:
      "Password must be 8+ chars with uppercase, lowercase, number, and special character",
  };

  const [user, setUser] = useState({
    name: "",
    username: "",
    roles: [],
    phoneNo: "",
    password: "",
  });

  useEffect(() => {
    const fetchRoles = async () => {
      try {
        const res = await axios.post("/api/role/list", {
          page: 0,
          sizePerPage: 10,
        });
        setRoles(res.data?.content || res.data);
      } catch (err) {
        console.error("Failed to load roles:", err);
      }
    };
    fetchRoles();
  }, []);

  useEffect(() => {
    const editData = localStorage.getItem("editUser");
    if (editData) {
      const parsed = JSON.parse(editData);
      setUser({
        name: parsed.name || "",
        username: parsed.username || "",
        roles: parsed.roles || [],
        phoneNo: parsed.phoneNo || "",
        password: "",
      });
      setEditMode(true);
      localStorage.removeItem("editUser");
    }
  }, []);

  const handleChange = (e) => {
    setUser((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setErrors((prev) => ({ ...prev, [e.target.name]: "" }));
  };

  const handleRoleChange = (e) => {
    const selected = Array.from(e.target.selectedOptions).map((opt) => opt.value);
    setUser((prev) => ({ ...prev, roles: selected }));
    setErrors((prev) => ({ ...prev, roles: "" }));
  };

  const validate = () => {
    const newErrors = {};

    if (!user.name.trim()) {
      newErrors.name = "Name is required";
    }

    if (!user.username.trim()) {
      newErrors.username = "Email is required";
    }

    if (!user.roles.length) {
      newErrors.roles = "Select at least one role";
    }

    if (!user.phoneNo.trim()) {
      newErrors.phoneNo = "Phone number is required";
    } else if (!/^\d{10}$/.test(user.phoneNo)) {
      newErrors.phoneNo = "Phone must be 10 digits";
    }

    if (!editMode) {
      if (!user.password.trim()) {
        newErrors.password = VALIDATION_MESSAGES.CREDENTIALS_REQUIRED;
      } else if (
        !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(
          user.password
        )
      ) {
        newErrors.password = VALIDATION_MESSAGES.CREDENTIALS_INVALID;
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    try {
      const url = editMode
        ? "http://localhost:8080/api/user/update"
        : "http://localhost:8080/api/user/register";

      const res = await axios.post(url, { ...user });

      if (
        res.data?.success === false ||
        res.data?.message?.toLowerCase().includes("already exist")
      ) {

        setErrors((prev) => ({
          ...prev,
          username: res.data.message || "Username already exists",
        }));
        return;
      }

      alert(editMode ? "Updated successfully" : "Registered successfully");

      setUser({
        name: "",
        username: "",
        roles: [],
        phoneNo: "",
        password: "",
      });
      setEditMode(false);
      router.push("/login");
    } catch (err) {
      const message =
        err.response?.data?.message || err.message || "Operation failed";

      const isDuplicate =
        message.toLowerCase().includes("already exists") ||
        message.toLowerCase().includes("already exist") ||
        message.toLowerCase().includes("duplicate");

      if (isDuplicate) {

        setErrors((prev) => ({
          ...prev,
          username: err.response?.data?.message || "Username already exists",
        }));
        return;
      }

      alert(message);
    }
  };

  const errorText = "text-red-500 text-[10px] mt-1";

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-100 via-gray-200 to-gray-100 flex justify-center items-start pt-10 px-4">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-xs bg-white p-4 rounded-lg shadow-md relative"
      >
        <div className="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-indigo-500 to-blue-500 rounded-t-lg" />

        <h2 className="text-center text-base font-bold text-gray-900 mb-3">
          {editMode ? "Update User" : "Registration"}
        </h2>

        <div className="mb-2">
          <label
            htmlFor="name"
            className="block text-left text-xs font-semibold text-gray-700 mb-1"
          >
            Name
          </label>
          <input
            id="name"
            type="text"
            name="name"
            value={user.name}
            onChange={handleChange}
            className="w-full p-1.5 text-xs rounded border border-gray-300 bg-gray-100"
          />
          {errors.name && <p className={errorText}>{errors.name}</p>}
        </div>

        <div className="mb-2">
          <label
            htmlFor="username"
            className="block text-left text-xs font-semibold text-gray-700 mb-1"
          >
            Email
          </label>
          <input
            id="username"
            type="email"
            name="username"
            value={user.username}
            onChange={handleChange}
            className="w-full p-1.5 text-xs rounded border border-gray-300 bg-gray-100"
          />
          {errors.username && <p className={errorText}>{errors.username}</p>}
        </div>

        <div className="mb-2">
          <label
            htmlFor="roles"
            className="block text-left text-xs font-semibold text-gray-700 mb-1"
          >
            Roles
          </label>
          <select
            id="roles"
            multiple
            name="roles"
            value={user.roles}
            onChange={handleRoleChange}
            className="w-full h-24 p-2 text-xs rounded border border-gray-300 bg-gray-100"
          >
            {roles.map((role) => (
              <option key={role.id} value={role.identifier}>
                {role.identifier}
              </option>
            ))}
          </select>
          {errors.roles && <p className={errorText}>{errors.roles}</p>}
        </div>

        <div className="mb-2">
          <label
            htmlFor="phoneNo"
            className="block text-left text-xs font-semibold text-gray-700 mb-1"
          >
            Phone
          </label>
          <input
            id="phoneNo"
            type="tel"
            name="phoneNo"
            value={user.phoneNo}
            onChange={handleChange}
            maxLength="10"
            className="w-full p-1.5 text-xs rounded border border-gray-300 bg-gray-100"
          />
          {errors.phoneNo && <p className={errorText}>{errors.phoneNo}</p>}
        </div>

        {!editMode && (
          <div className="mb-2">
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
              value={user.password}
              onChange={handleChange}
              className="w-full p-1.5 text-xs rounded border border-gray-300 bg-gray-100"
            />
            {errors.password && <p className={errorText}>{errors.password}</p>}
          </div>
        )}

        <button
          type="submit"
          className="w-full mt-2 py-1.5 text-xs rounded-md text-white font-semibold bg-gradient-to-r from-indigo-500 to-blue-500"
        >
          {editMode ? "Update" : "Register"}
        </button>

        <div className="text-center text-[10px] text-gray-500 mt-2">
          Already have an account?{" "}
          <Link href="/login" className="text-blue-600 hover:underline">
            Login
          </Link>
        </div>
      </form>
    </div>
  );
}