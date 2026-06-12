// components/add/BaseAddForm.jsx

"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import { useApiWithLoader } from "../../app/lib/useApiWithLoader";

export default function BaseAddForm({
  title,
  apiPath,
  extraFields = [],
  extraData: externalExtraData = {},
  identifierKey = "identifier",
}) {
  if (!title) console.warn("BaseAddForm: 'title' prop is required");
  if (!apiPath) console.warn("BaseAddForm: 'apiPath' prop is required");

  const router = useRouter();
  const { post } = useApiWithLoader();

  const [identifierValue, setIdentifierValue] = useState("");
  const [extraData, setExtraData] = useState({});
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const displayLabel = identifierKey.charAt(0).toUpperCase() + identifierKey.slice(1);

  function handleExtraChange(key, value) {
    setExtraData((prev) => ({ ...prev, [key]: value }));
  }

  function handleMultiToggle(key, value) {
    setExtraData((prev) => {
      const current = prev[key] || [];
      const updated = current.includes(value)
        ? current.filter((v) => v !== value)
        : [...current, value];
      return { ...prev, [key]: updated };
    });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);

    try {
      const payload = {
        [identifierKey]: identifierValue,
        ...extraData,
        ...externalExtraData,
      };

      const data = await post(`/${apiPath}/add`, payload);

      if (data?.success === false) {
        const backendMessage = data.message || `${title} with this ${identifierKey} already exists.`;
        setError(backendMessage);
        console.warn("Creation blocked by backend validation:", backendMessage);
        return;
      }

      const hasIdentifier = data && (data[identifierKey] || data.identifier);

      if (hasIdentifier || data?.success === true) {
        setSuccess(`${title} added successfully`);
        setTimeout(() => router.back(), 1500);
      } else {
        setError("Failed to add. Please try again.");
      }
    } catch (err) {
      console.error(" Request network/server exception:", err);
      const errorMsg = err.response?.data?.message || "Unable to connect to server";
      setError(errorMsg);
    } finally {
      setLoading(false);
    }
  }

  const renderFieldInput = (field) => {
    if (field.type === "custom") {
      return field.component;
    }

    if (field.type === "select") {
      return (
        <select
          className="mt-1 w-full px-4 py-2 border rounded-md text-sm border-[#006E74]/30 focus:border-[#006E74] focus:ring-1 focus:ring-[#006E74]"
          onChange={(e) => handleExtraChange(field.key, e.target.value)}
          defaultValue=""
        >
          <option value="" disabled>Select {field.label}</option>
          {field.options?.map((opt) => (
            <option key={opt.value} value={opt.value}>{opt.label}</option>
          ))}
        </select>
      );
    }

    if (field.type === "multiselect") {
      return (
        <div className="flex flex-wrap gap-2 mt-2">
          {field.options?.map((opt) => {
            const isSelected = (extraData[field.key] || []).includes(opt.value);
            const btnClass = isSelected
              ? "bg-[#0097AC] text-white border-[#0097AC]"
              : "bg-white text-[#231F20] border-[#006E74]/30";

            return (
              <button
                key={opt.value}
                type="button"
                onClick={() => handleMultiToggle(field.key, opt.value)}
                className={`px-3 py-1 text-xs rounded-md border transition ${btnClass}`}
              >
                {opt.label}
              </button>
            );
          })}
        </div>
      );
    }

    const inputType = field.type || "text";
    return (
      <input
        className="mt-1 w-full px-4 py-2 border rounded-md text-sm border-[#006E74]/30 focus:border-[#006E74] focus:ring-1 focus:ring-[#006E74] outline-none"
        type={inputType}
        placeholder={`Enter ${field.label ? field.label.toLowerCase() : field.key}`}
        onChange={(e) => handleExtraChange(field.key, e.target.value)}
      />
    );
  };

  const submitBtnClass = loading
    ? "bg-[#006E74]/50 cursor-not-allowed"
    : "bg-[#006E74] hover:bg-[#0097AC]";
  const submitBtnText = loading ? "Saving..." : `Add ${title}`;

  return (
    <div className="min-h-screen bg-white p-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-[#231F20]">Add {title}</h1>
        <p className="text-sm text-[#0097AC] mt-1">Fill in the details below</p>
      </div>

      {error && (
        <div className="mb-4 px-4 py-3 border text-sm font-medium rounded-md bg-white text-red-600 border-red-300">
          {error}
        </div>
      )}
      {success && (
        <div className="mb-4 px-4 py-3 border text-sm font-medium rounded-md bg-white text-green-600 border-green-300">
          {success}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
          <div>
            <label htmlFor="id-field" className="text-xs font-semibold text-[#006E74] uppercase">
              {displayLabel}
            </label>
            <input
              id="id-field"
              className="mt-1 w-full px-4 py-2 border rounded-md text-sm border-[#006E74]/30 focus:border-[#006E74] focus:ring-1 focus:ring-[#006E74] outline-none"
              type="text"
              value={identifierValue}
              onChange={(e) => setIdentifierValue(e.target.value)}
              placeholder={`Enter ${identifierKey}`}
              required
            />
          </div>

          {extraFields.map((field) => (
            <div key={field.key}>
              {field.type !== "custom" && (
                <label htmlFor={`field-${field.key}`} className="text-xs font-semibold text-[#006E74] uppercase">
                  {field.label || field.key}
                </label>
              )}
              <div id={`field-${field.key}`}>{renderFieldInput(field)}</div>
            </div>
          ))}
        </div>

        <div className="sticky bottom-0 bg-white pt-4 border-t border-[#006E74]/20 flex justify-end gap-3">
          <button
            type="button"
            onClick={() => router.back()}
            className="px-6 py-2 text-sm border rounded-md text-[#231F20] border-[#006E74]/30"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={loading}
            className={`px-6 py-2 text-sm rounded-md text-white ${submitBtnClass}`}
          >
            {submitBtnText}
          </button>
        </div>
      </form>
    </div>
  );
}

BaseAddForm.propTypes = {
  title: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  extraFields: PropTypes.array,
  extraData: PropTypes.object,
  identifierKey: PropTypes.string,
};