"use client";
import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import axios from "axios";
import { useRouter } from "next/navigation";
import CommonDropdown from "@/app/components/CommonDropDown";
import Layout from "@/app/components/Layout";

function hasRejectMessage(message) {
  return (
    message.includes("already") ||
    message.includes("exist") ||
    message.includes("fail")
  );
}

export default function AddFormSkeleton({
  title = "",
  apiPath = "",
  fields = [],
}) {
  const router = useRouter();
  const BASE_URL = "/api";

  const [token, setToken] = useState("");
  const [tokenReady, setTokenReady] = useState(false);

  const [identifier, setIdentifier] = useState("");
  const [formData, setFormData] = useState({});
  const [dropdownOptions, setDropdownOptions] = useState({});
  const [dropdownLoading, setDropdownLoading] = useState({});

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const [showPassword, setShowPassword] = useState({});

  const fieldsDependency = JSON.stringify(fields);

  useEffect(() => {
    const storedToken = localStorage.getItem("token") || "";
    setToken(storedToken);
    setTokenReady(true);
  }, []);

  useEffect(() => {
    if (!tokenReady || !token) return;

    loadDropdowns();
  }, [fieldsDependency, token, tokenReady]);

  const loadDropdowns = async () => {
    const selectFields = fields.filter(isSelectableField);
    await Promise.all(selectFields.map(fetchDropdownData));
  };

  const isSelectableField = (field) =>
    field.type === "select" && field.api;

  const getTargetUrl = (field) => {
    return field.endpoint
      ? `${BASE_URL}/${field.api}/${field.endpoint}`
      : `${BASE_URL}/${field.api}/findByStatus`;
  };

  const fetchDropdownData = async (field) => {
    setDropdownLoading((prev) => ({ ...prev, [field.name]: true }));

    try {
      const url = getTargetUrl(field);

      const res = await axios.get(url, {
        headers: { Authorization: `Bearer ${token}` },
      });

      const extractedData = res.data?.data || res.data || [];

      setDropdownOptions((prev) => ({
        ...prev,
        [field.name]: extractedData,
      }));
    } catch (err) {
      console.error(`Error filling dropdown for [${field.name}]:`, err);
    } finally {
      setDropdownLoading((prev) => ({
        ...prev,
        [field.name]: false,
      }));
    }
  };

  function handleValueChange(field, event) {
    const { name, value, options, selectedOptions } = event.target;
    const isMultiple = field.multiple || event.target.multiple;

    if (isMultiple) {
      const targetOptions =
        selectedOptions ||
        (options ? Array.from(options).filter((o) => o.selected) : []);
      const selectedValues = Array.from(targetOptions).map((opt) => opt.value);
      setFormData((prev) => ({ ...prev, [name]: selectedValues }));
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }
  }

  function togglePasswordVisibility(fieldName) {
    setShowPassword((prev) => ({ ...prev, [fieldName]: !prev[fieldName] }));
  }

  function getValidationError() {
    for (const field of fields) {
      const value = formData[field.name];
      if (!field.validation?.regex) continue;
      const stringValue = value ? String(value) : "";
      if (!field.validation.regex.test(stringValue)) {
        return field.validation.message || `Invalid input for ${field.label}`;
      }
    }
    return "";
  }

  function handleResponseData(data) {
  if (!data) {
    setError("Unexpected server response");
    return;
  }

  if (data.success === false) {
    setError(data.message || `Failed to save ${title}`);
    return;
  }

  if (data.success === true) {
    setSuccess(data.message || `${title} successfully saved.`);
    setTimeout(() => router.back(), 1500);
    return;
  }
  setError("Unknown response from server");
}

  function handleSubmissionError(err) {
  console.error("Submission Error:", err);

  if (err.response?.data) {
    const data = err.response.data;

    if (data.success === false) {
      setError(data.message);
      return;
    }

    const msg = data.message || data.error;
    setError(msg || `Failed to save ${title}`);
    return;
  }

  setError("Network error. Please try again.");
}

 async function handleSubmit(e) {
  e.preventDefault();
  setError("");
  setSuccess("");

  const validationError = getValidationError();
  if (validationError) {
    setError(validationError);
    return;
  }

  setLoading(true);

  try {
    const res = await axios.post(
      `${BASE_URL}/${apiPath}/add`,
      { identifier, ...formData },
      { headers: { Authorization: `Bearer ${token}` } }
    );

    handleResponseData(res.data);
  } catch (err) {
    handleSubmissionError(err);
  } finally {
    setLoading(false);
  }
}


  return (
    <Layout>
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-6 font-sans text-left select-none">
        <div className="w-full max-w-lg bg-white rounded-2xl border border-slate-200/80 p-8 shadow-sm">

          <div className="mb-6">
            <h2 className="text-xl font-bold text-slate-900 tracking-tight">Add New {title}</h2>
            <p className="text-xs text-slate-500 mt-1">Configure parameters for your system record below.</p>
          </div>

          {error && (
            <div className="bg-rose-50 border border-rose-200 text-rose-800 text-xs font-medium p-3 rounded-xl mb-4 flex items-center gap-2">
              ⚠️ {error}
            </div>
          )}
          {success && (
            <div className="bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs font-medium p-3 rounded-xl mb-4 flex items-center gap-2">
              ✓ {success}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">

            <div>
              <label
                htmlFor="identifier"
                className="text-[11px] font-semibold uppercase tracking-wider text-slate-500 block mb-1.5"
              >
                System Identifier
              </label>

              <input
                id="identifier"
                type="text"
                placeholder="e.g., ITEM_CODE_01"
                value={identifier}
                onChange={(e) => setIdentifier(e.target.value)}
                className="w-full border border-slate-200 bg-slate-50 text-slate-800 p-2.5 rounded-lg text-sm placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all font-mono"
                required
              />
            </div>

            {fields.map((field) => {
              let content;

              if (field.type === "select") {
                if (dropdownLoading[field.name]) {
                  content = (
                    <div className="text-xs text-slate-400 italic py-2">
                      Loading options...
                    </div>
                  );
                } else {
                  content = (
                    <CommonDropdown
                      label={field.label}
                      name={field.name}
                      options={dropdownOptions[field.name] || []}
                      value={formData[field.name] || (field.multiple ? [] : "")}
                      multiple={field.multiple || false}
                      optionLabel={field.optionLabel || "name"}
                      optionValue={field.optionValue || "identifier"}
                      onChange={(e) => handleValueChange(field, e)}
                    />
                  );
                }
              } else if (field.type === "password") {
                const isVisible = showPassword[field.name];

                content = (
                  <div>
                    <label
                      htmlFor={field.name}
                      className="text-[11px] font-semibold uppercase tracking-wider text-slate-500 block mb-1.5"
                    >
                      {field.label}
                    </label>

                    <div className="relative">
                      <input
                        id={field.name}
                        type={isVisible ? "text" : "password"}
                        name={field.name}
                        placeholder={field.placeholder || `Enter ${field.label.toLowerCase()}...`}
                        value={formData[field.name] || ""}
                        onChange={(e) => handleValueChange(field, e)}
                        required={field.required || false}
                        className="w-full border border-slate-200 bg-slate-50 text-slate-800 p-2.5 pr-10 rounded-lg text-sm"
                      />

                      <button
                        type="button"
                        onClick={() => togglePasswordVisibility(field.name)}
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 text-xs"
                      >
                        {isVisible ? "Hide" : "Show"}
                      </button>
                    </div>
                  </div>
                );
              } else {
                content = (
                  <div>
                    <label
                      htmlFor={field.name}
                      className="text-[11px] font-semibold uppercase tracking-wider text-slate-500 block mb-1.5"
                    >
                      {field.label}
                    </label>

                    <input
                      id={field.name}
                      type={field.type || "text"}
                      name={field.name}
                      placeholder={field.placeholder || `Enter ${field.label.toLowerCase()}...`}
                      value={formData[field.name] || ""}
                      onChange={(e) => handleValueChange(field, e)}
                      required={field.required || false}
                      className="w-full border border-slate-200 bg-slate-50 text-slate-800 p-2.5 rounded-lg text-sm"
                    />
                  </div>
                );
              }

              return <div key={field.name}>{content}</div>;
            })}

            <div className="flex gap-3 pt-2">
              <button
                type="button"
                onClick={() => router.back()}
                className="flex-1 bg-slate-100 text-slate-600 font-medium text-sm py-2.5 rounded-lg hover:bg-slate-200 transition-colors"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={loading}
                className="flex-1 bg-blue-600 text-white font-medium text-sm py-2.5 rounded-lg hover:bg-blue-700 shadow-sm shadow-blue-500/10 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {loading ? "Saving..." : `Add ${title}`}
              </button>
            </div>

          </form>
        </div>
      </div>
    </Layout>
  );
}

AddFormSkeleton.propTypes = {
  title: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      label: PropTypes.string,
      type: PropTypes.string.isRequired,
      required: PropTypes.bool,
      placeholder: PropTypes.string,
      validation: PropTypes.shape({
        regex: PropTypes.instanceOf(RegExp),
        message: PropTypes.string,
      }),
    })
  ).isRequired,
};
