"use client";

import { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import PropTypes from "prop-types";
import api from "../api";
import { SingleDropdown, MultiDropdown } from "./Dropdown";

const mapFieldValue = (field, value) => {
  if (value === null || value === undefined) return undefined;

  if (field.type === "multiDropdown") {
    return Array.isArray(value)
      ? value.map((item) => item?.identifier || item)
      : [];
  }

  if (field.type === "singleDropdown") {
    return value?.identifier || value || "";
  }

  if (field.type === "select") {
    return value?.value || value || "";
  }

  return value;
};

const Edit = ({ urlName, fields, identifier = "identifier" }) => {
  const router = useRouter();
  const searchParams = useSearchParams();
  const identifierValue = searchParams.get("identifier");

  const [formData, setFormData] = useState({});
  const [message, setMessage] = useState("");
  const [errors, setErrors] = useState({});

  useEffect(() => {
    const fetchData = async () => {
      const initial = {};

      fields.forEach((field) => {
        initial[field.name] =
          field.type === "multiDropdown" ? [] : "";
      });

      try {
        const res = await api.get(
          `/${urlName}/get?identifier=${identifierValue}`
        );

        const data = res.data;

        const transformedData = {
          ...initial,
          id: data.id,
          identifier: data.identifier,
        };

        fields.forEach((field) => {
          const mapped = mapFieldValue(
            field,
            data[field.name]
          );

          if (mapped !== undefined) {
            transformedData[field.name] = mapped;
          }
        });

        setFormData(transformedData);
      } catch (err) {
        console.error(err);
        setMessage("Failed to load data");
      }
    };

    if (identifierValue) fetchData();
  }, [identifierValue, fields, urlName]);

  useEffect(() => {
    if (!message) return;
    const timer = setTimeout(() => setMessage(""), 3000);
    return () => clearTimeout(timer);
  }, [message]);

  useEffect(() => {
    if (Object.keys(errors).length === 0) return;
    const timer = setTimeout(() => setErrors({}), 3000);
    return () => clearTimeout(timer);
  }, [errors]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    const field = fields.find((f) => f.name === name);

    let newValue = value;

    if (field?.validation === "phone") {
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

  const validateForm = () => {
    const newErrors = {};

    fields.forEach((field) => {
      const isReadOnly =
        field.readOnly || field.name === identifier;

      if (isReadOnly) return;

      const value = formData[field.name];

      if (field.required !== false) {
        if (
          value === "" ||
          value === null ||
          value === undefined ||
          (Array.isArray(value) && value.length === 0)
        ) {
          newErrors[field.name] =
            `${field.label} is required`;
          return;
        }
      }

      if (!value) return;

      if (field.validation === "email") {
        if (
          !/^[a-zA-Z0-9._%+-]+@gmail\.com$/.test(
            String(value).trim()
          )
        ) {
          newErrors[field.name] =
            "Enter valid Gmail address";
        }
      }

      if (field.validation === "phone") {
        if (!/^\d{10}$/.test(String(value))) {
          newErrors[field.name] =
            "Phone number must be 10 digits";
        }
      }

      if (field.validation === "password") {
        if (String(value).length < 6) {
          newErrors[field.name] =
            "Password must be at least 6 characters";
        }
      }
    });

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) return;

    try {
      const payload = {
        ...formData,
        id: formData.id,
        identifier: formData.identifier,
      };

      if (!payload.password?.trim()) {
        delete payload.password;
      }

      const res = await api.post(
        `/${urlName}/update`,
        payload
      );

      if (res.data?.success === false) {
        setMessage(res.data.message);
        return;
      }

      setMessage("Updated successfully");

      setTimeout(() => {
        router.push(`/${urlName}/list`);
      }, 1000);
    } catch (err) {
      const errorMessage =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        "Update failed";

      setMessage(errorMessage);
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
          Edit {urlName}
        </h2>

        {message && (
          <p
            className={`text-center text-sm mb-4 ${
              message.includes("success")
                ? "text-green-600"
                : "text-red-500"
            }`}
          >
            {message}
          </p>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          {fields.map((field) => {
            const isReadOnly =
              field.readOnly || field.name === identifier;

            let inputType = "text";
            if (field.validation === "password")
              inputType = "password";
            else if (field.validation === "phone")
              inputType = "tel";

            return (
              <div key={field.name}>
                <label className="block mb-1 font-medium">
                  {field.label}
                  {!isReadOnly &&
                    field.required !== false && (
                      <span className="text-red-500 ml-1">
                        *
                      </span>
                    )}
                </label>

                {(field.type === "text" ||
                  field.type === "number") && (
                  <input
                    type={inputType}
                    name={field.name}
                    value={formData[field.name] || ""}
                    onChange={handleChange}
                    disabled={isReadOnly}
                    maxLength={
                      field.validation === "phone"
                        ? 10
                        : undefined
                    }
                    className={`w-full p-3 rounded border ${
                      errors[field.name]
                        ? "border-red-500"
                        : "border-gray-300"
                    } ${
                      isReadOnly
                        ? "bg-gray-200 cursor-not-allowed"
                        : ""
                    }`}
                  />
                )}

                {field.type === "singleDropdown" && (
                  <SingleDropdown
                    name={field.name}
                    value={formData[field.name] || ""}
                    onChange={handleChange}
                    apiUrl={field.api}
                    disabled={isReadOnly}
                  />
                )}

                {field.type === "multiDropdown" && (
                  <MultiDropdown
                    name={field.name}
                    value={formData[field.name] || []}
                    onChange={handleChange}
                    apiUrl={field.api}
                    disabled={isReadOnly}
                  />
                )}

                {errors[field.name] && (
                  <p className="text-red-500 text-xs mt-1">
                    {errors[field.name]}
                  </p>
                )}
              </div>
            );
          })}

          <button
            type="submit"
            className="w-full bg-green-600 hover:bg-green-700 text-white p-3 rounded transition"
          >
            Update
          </button>
        </form>
      </div>
    </div>
  );
};

Edit.propTypes = {
  urlName: PropTypes.string.isRequired,
  identifier: PropTypes.string,
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      label: PropTypes.string,
      type: PropTypes.string,
      validation: PropTypes.string,
      readOnly: PropTypes.bool,
      api: PropTypes.string,
      required: PropTypes.bool, 
    })
  ).isRequired,
};

export default Edit;