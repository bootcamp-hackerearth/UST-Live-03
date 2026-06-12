"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import api from "../api";
import { SingleDropdown, MultiDropdown } from "./Dropdown";

const Add = ({ urlName, fields }) => {
  const router = useRouter();

  const [formData, setFormData] = useState({});
  const [message, setMessage] = useState("");
  const [errors, setErrors] = useState({});

  useEffect(() => {
    const initial = {};

    fields.forEach((field) => {
      if (field.type === "multiDropdown") {
        initial[field.name] = [];
      } else if (field.required === false) {
        initial[field.name] = null;
      } else {
        initial[field.name] = "";
      }
    });

    setFormData(initial);
  }, [fields]);

  useEffect(() => {
    if (message) {
      const timer = setTimeout(() => setMessage(""), 3000);
      return () => clearTimeout(timer);
    }
  }, [message]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    let newValue = value;

    if (
      name.toLowerCase().includes("phone") ||
      name.toLowerCase().includes("mobile")
    ) {
      newValue = value.replaceAll(/\D/g, "").slice(0, 10);
    }

    setFormData((prev) => ({
      ...prev,
      [name]: newValue,
    }));

    setErrors((prev) => ({
      ...prev,
      [name]: "",
    }));
  };

  const isEmpty = (value) =>
    value === "" ||
    value === null ||
    value === undefined ||
    (Array.isArray(value) && value.length === 0);

  const validateField = (field, value) => {
    if (field.required !== false && isEmpty(value)) {
      return `${field.label} is required`;
    }

    if (!value) return "";

    if (field.type === "email") {
      if (!/^[a-zA-Z0-9._%+-]+@gmail\.com$/.test(value.trim())) {
        return "Email must be like example@gmail.com";
      }
    }

    if (field.type === "password") {
      if (value.length < 6) {
        return "Password must be at least 6 characters";
      }
    }

    if (
      field.name.toLowerCase().includes("phone") ||
      field.name.toLowerCase().includes("mobile")
    ) {
      if (!/^\d{10}$/.test(value)) {
        return "Phone number must be exactly 10 digits";
      }
    }

    return "";
  };

  const validateForm = () => {
    const newErrors = {};

    fields.forEach((field) => {
      const error = validateField(field, formData[field.name]);
      if (error) {
        newErrors[field.name] = error;
      }
    });

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      setMessage("Please correct the errors");
      return;
    }

    try {
      const res = await api.post(`/${urlName}/add`, formData);
      const responseData = res.data;

      if (
        (typeof responseData === "string" &&
          responseData.toLowerCase().includes("exist")) ||
        responseData?.message?.toLowerCase().includes("exist")
      ) {
        setMessage("Email already exists");

        setErrors((prev) => ({
          ...prev,
          username: "Email already exists",
        }));

        return;
      }

      setMessage("Added successfully");

      setTimeout(() => {
        router.push(`/${urlName}/list`);
      }, 1000);
    } catch (err) {
      console.error("ADD ERROR:", err.response || err);
      setMessage("Add failed");
    }
  };

  return (
    <div className="min-h-screen flex justify-center items-center bg-gray-100 p-6">
      <div className="bg-white p-8 rounded-xl shadow-xl w-full max-w-md">
        <button
          onClick={() => router.push(`/${urlName}/list`)}
          className="mb-4 text-gray-500 text-sm hover:text-gray-700"
        >
          ← Back to List
        </button>

        <h2 className="text-xl font-bold text-center mb-4">
          Add {urlName}
        </h2>

        {message && (
          <p
            className={`text-center text-sm mb-4 ${
              message.toLowerCase().includes("success")
                ? "text-green-600"
                : "text-red-500"
            }`}
          >
            {message}
          </p>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          {fields.map((field) => (
            <div key={field.name}>
              <label className="block mb-1 font-medium">
                {field.label}
                {field.required !== false && (
                  <span className="text-red-500 ml-1">*</span>
                )}
              </label>

              {(field.type === "text" ||
                field.type === "email" ||
                field.type === "password" ||
                field.type === "number") && (
                <input
                  type={
                    field.name.toLowerCase().includes("phone")
                      ? "tel"
                      : field.type
                  }
                  name={field.name}
                  value={formData[field.name] || ""}
                  onChange={handleChange}
                  placeholder={field.label}
                  className={`w-full p-3 rounded border ${
                    errors[field.name]
                      ? "border-red-500"
                      : "border-gray-300"
                  }`}
                />
              )}

              {field.type === "select" && (
                <select
                  name={field.name}
                  value={formData[field.name] || ""}
                  onChange={handleChange}
                  className={`w-full p-3 rounded border ${
                    errors[field.name]
                      ? "border-red-500"
                      : "border-gray-300"
                  }`}
                >
                  <option value="">Select {field.label}</option>
                  {field.options.map((opt) => (
                    <option key={opt.value} value={opt.value}>
                      {opt.label}
                    </option>
                  ))}
                </select>
              )}

              {field.type === "singleDropdown" && (
                <SingleDropdown
                  name={field.name}
                  value={formData[field.name]}
                  onChange={handleChange}
                  apiUrl={field.api}
                />
              )}

              {field.type === "multiDropdown" && (
                <MultiDropdown
                  name={field.name}
                  value={formData[field.name]}
                  onChange={handleChange}
                  apiUrl={field.api}
                />
              )}

              {errors[field.name] && (
                <p className="text-red-500 text-xs mt-1">
                  {errors[field.name]}
                </p>
              )}
            </div>
          ))}

          <button
            type="submit"
            className="w-full bg-green-600 text-white p-3 rounded hover:bg-green-700"
          >
            Add
          </button>
        </form>
      </div>
    </div>
  );
};

Add.propTypes = {
  urlName: PropTypes.string.isRequired,
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      label: PropTypes.string,
      type: PropTypes.string,
      options: PropTypes.array,
      api: PropTypes.string,
      required: PropTypes.bool,
    })
  ).isRequired,
};

export default Add;