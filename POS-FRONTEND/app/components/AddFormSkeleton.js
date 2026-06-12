"use client";
import { useState, useEffect, useCallback } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import api from "./Axios";

const baseInputClass = "py-2 px-3 border-[1.5px] rounded-lg text-sm outline-none bg-[#fafaf8] box-border w-full transition-all focus:border-brand";
const fieldErrorClass = "text-[11px] text-[#e53e3e] mt-0.5";
const fieldLabelClass = "text-xs md:text-sm font-semibold text-gray-600";
const fieldWrapClass = "flex flex-col gap-1.25";

function getBorderClass(hasError) {
  return hasError ? "border-[#e53e3e]" : "border-gray-300";
}

function FieldError({ message }) {
  if (!message) return null;
  return <span className={fieldErrorClass}>{message}</span>;
}

FieldError.propTypes = {
  message: PropTypes.string,
};

FieldError.defaultProps = {
  message: "",
};

function StatusBanner({ type, message }) {
  const styles =
    type === "error"
      ? "bg-[#fff5f5] border-[#fca5a5] text-[#c53030]"
      : "bg-[#f0fff4] border-[#9ae6b4] text-[#276749]";
  return (
    <div className={`${styles} border rounded-lg p-2.5 md:p-3.5 text-xs md:text-sm mb-4 text-center col-span-2`}>
      {message}
    </div>
  );
}

StatusBanner.propTypes = {
  type: PropTypes.oneOf(["error", "success"]).isRequired,
  message: PropTypes.string.isRequired,
};

export default function AddFormSkeleton({
  title,
  apiPath,
  extraFields = [],
  extraData: externalExtraData = {},
}) {

  const router = useRouter();
  const [identifier, setIdentifier] = useState("");
  const [extraData, setExtraData] = useState({});
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);
  const [identifierError, setIdentifierError] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});
  const [activeDropdownKey, setActiveDropdownKey] = useState(null);
  const hasCustomIdentifier = extraFields.some((f) => f.key === "identifier");

  function clearFieldError(key) {
    setFieldErrors((prev) => ({ ...prev, [key]: "" }));
  }

  const handleClickOutside = useCallback(() => {
    setActiveDropdownKey(null);
  }, []);

  useEffect(() => {
    document.addEventListener("click", handleClickOutside);
    return () => document.removeEventListener("click", handleClickOutside);
  }, [handleClickOutside]);

  function handleExtraChange(key, value) {
    setExtraData((prev) => ({ ...prev, [key]: value }));
    if (fieldErrors[key]) clearFieldError(key);
  }

  function handleMultiToggle(key, value) {
    setExtraData((prev) => {
      const current = prev[key] || [];
      const updated = current.includes(value)
        ? current.filter((v) => v !== value)
        : [...current, value];
      return { ...prev, [key]: updated };
    });
    if (fieldErrors[key]) clearFieldError(key);
  }

  function handleIdentifierChange(e) {
    setIdentifier(e.target.value);
    if (identifierError) setIdentifierError(false);
    if (error) setError("");
    if (loading) setLoading(false);
    if (fieldErrors.identifier) clearFieldError("identifier");
  }

  function validate() {
    const errors = {};
    if (!hasCustomIdentifier && !identifier.trim()) {
      errors.identifier = "Identifier is required.";
    }

    extraFields.forEach((field) => {
      if (field.type === "custom") return;
      if (field.type === "multiselect") {
        const val = extraData[field.key] || [];
        if (val.length === 0) errors[field.key] = `${field.label} is required.`;
      } else {
        const val = extraData[field.key] || "";
        if (!String(val).trim()) errors[field.key] = `${field.label} is required.`;
      }
    });

    extraFields.forEach((field) => {
      if (field.type !== "custom") return;
      if (field.optional) return;
      const val = externalExtraData[field.key];
      const isEmpty =
        val === undefined ||
        val === null ||
        val === "" ||
        (Array.isArray(val) && val.length === 0);
      if (isEmpty) errors[field.key] = `${field.label || field.key} is required.`;
    });
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSuccess("");
    setIdentifierError(false);
    if (!validate()) return;
    setLoading(true);
    const payload = {
      identifier: hasCustomIdentifier ? externalExtraData.identifier : identifier,
      ...extraData,
      ...externalExtraData,
    };

    try {
      const res = await api.post(`/${apiPath}/add`, payload);
      const data = res.data;
      if (data.success === false) {
        setError(data.message || "Identifier already exists. Please use a different one.");
        setIdentifierError(true);
        setLoading(false);
        return;
      }
      if (data.identifier) {
        setSuccess(`${title} added successfully`);
        setTimeout(() => router.back(), 1500);
      } else {
        setError("Failed to add. Please try again.");
      }
    } catch {
      setError("Unable to connect to server. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  function renderFieldContent(field) {
    const borderClass = getBorderClass(fieldErrors[field.key]);
    const fieldErr = <FieldError message={fieldErrors[field.key]} />;

    if (field.type === "custom") {
      return (
        <>
          {typeof field.component === "function"
            ? field.component({
                isOpen: activeDropdownKey === field.key,
                setOpen: (open) => setActiveDropdownKey(open ? field.key : null),
              })
            : field.component}
          {fieldErr}
        </>
      );
    }
    if (field.type === "select") {
      return (
        <>
          <select
            className={`${baseInputClass} ${borderClass}`}
            onChange={(e) => handleExtraChange(field.key, e.target.value)}
          >
            <option value="">Select {field.label}</option>
            {field.options?.map((opt) => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
          {fieldErr}
        </>
      );
    }
    if (field.type === "multiselect") {
      return (
        <>
          <div className={`flex flex-wrap gap-2 p-2.5 border-[1.5px] rounded-lg bg-[#fafaf8] min-height-[44px] ${borderClass}`}>
            {field.options?.map((opt) => {
              const isSelected = (extraData[field.key] || []).includes(opt.value);
              return (
                <button
                  key={opt.value}
                  type="button"
                  onClick={() => handleMultiToggle(field.key, opt.value)}
                  className={`py-1 px-3 rounded-2xl border-[1.5px] text-xs md:text-sm cursor-pointer transition-all ${
                    isSelected
                      ? "bg-brand border-brand text-white font-semibold"
                      : "bg-white border-gray-300 text-gray-700 font-medium hover:bg-gray-50"
                  }`}
                >
                  {isSelected ? "✓ " : ""}{opt.label}
                </button>
              );
            })}
          </div>
          {fieldErr}
        </>
      );
    }
    return (
      <>
        <input
          className={`${baseInputClass} ${borderClass}`}
          type={field.type || "text"}
          placeholder={`Enter ${field.label}`}
          onChange={(e) => handleExtraChange(field.key, e.target.value)}
        />
        {fieldErr}
      </>
    );
  }

  return (
    <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-[#f9fafb] font-sans flex flex-col overflow-hidden">
      <div className="flex-1 p-5 md:p-6 flex flex-col overflow-hidden">
        <div className="flex items-center gap-3 mb-4 shrink-0 relative">
          <button
            type="button"
            className="py-2 px-4 bg-transparent text-brand border-[1.5px] border-brand rounded-lg text-xs md:text-sm font-semibold cursor-pointer shrink-0 transition-colors hover:bg-brand/5"
            onClick={() => router.back()}
          >
            &larr; Back
          </button>
          <h2 className="absolute left-1/2 -translate-x-1/2 m-0 text-xl font-bold text-brand whitespace-nowrap">
            Add {title}
          </h2>
        </div>
        <div className="flex-1 flex items-center justify-center overflow-hidden">
          <div className="bg-white rounded-xl shadow-[0_2px_10px_rgba(0,0,0,0.06)] p-6 md:p-8 w-full max-w-[720px] max-h-full overflow-auto">
            <p className="text-base md:text-[17px] font-bold text-[#1a1a1a] m-0 mb-1">New {title}</p>
            <p className="text-xs md:text-sm text-gray-400 mb-5">Fill in the details below</p>
            {error && <StatusBanner type="error" message={error} />}
            {success && <StatusBanner type="success" message={success} />}
            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-3.5">
              {!hasCustomIdentifier && (
                <div className={fieldWrapClass}>
                  <label className={fieldLabelClass} htmlFor="identifier">Identifier</label>
                  <input
                    id="identifier"
                    className={`${baseInputClass} ${getBorderClass(identifierError || fieldErrors.identifier)}`}
                    type="text"
                    placeholder="Enter identifier"
                    value={identifier}
                    onChange={handleIdentifierChange}
                  />
                  <FieldError message={fieldErrors.identifier} />
                </div>
              )}
              {extraFields.map((field) => (
                <div key={field.key} className={fieldWrapClass}>
                  {field.type !== "custom" && (
                    <label className={fieldLabelClass}>{field.label}</label>
                  )}
                  {renderFieldContent(field)}
                </div>
              ))}
              <div className="flex gap-3 mt-2 col-span-1 md:col-span-2">
                <button
                  type="button"
                  className="flex-1 p-2.5 bg-gray-100 text-gray-600 border-none rounded-lg text-sm font-semibold cursor-pointer transition-colors hover:bg-gray-200"
                  onClick={() => router.back()}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className={`flex-1 p-2.5 text-white border-none rounded-lg text-sm font-semibold transition-all ${
                    loading
                      ? "bg-gray-400 cursor-not-allowed"
                      : "bg-brand cursor-pointer hover:bg-brand-hover"
                  }`}
                  disabled={loading}
                >
                  {loading ? "Saving\u2026" : `Add ${title}`}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}

AddFormSkeleton.propTypes = {
  title: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  extraFields: PropTypes.array,
  extraData: PropTypes.object,
};