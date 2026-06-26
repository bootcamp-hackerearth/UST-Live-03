"use client";

import React, { useEffect, useRef, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import PropTypes from "prop-types";
import api from "./Axios";

function formatDateTime(raw) {
  if (!raw) return "—";
  const d = new Date(raw);
  return d.toLocaleString("en-IN", {
    day: "2-digit", month: "short", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  });
}

export default function EditFormSkeleton({
  title = "",
  apiPath,
  extraFields = [],
  externalExtraData = {},
  setters = {},
}) {
  const router = useRouter();
  const params = useParams();
  const identifier = params.identifier ? decodeURIComponent(params.identifier) : "";

  const [identifierDisplay, setIdentifierDisplay] = useState("");
  const [extraData, setExtraData] = useState({});
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});
  const [activeDropdownKey, setActiveDropdownKey] = useState(null);

  const [auditInfo, setAuditInfo] = useState({
    createdBy: null,
    createdOn: null,
    modifiedBy: null,
    modifiedOn: null,
  });

  const extraFieldsRef = useRef(extraFields);
  const settersRef = useRef(setters);
  extraFieldsRef.current = extraFields;
  settersRef.current = setters;

  useEffect(() => {
    if (externalExtraData && Object.keys(externalExtraData).length > 0) {
      setExtraData((prev) => ({ ...prev, ...externalExtraData }));
    }
  }, [externalExtraData]);

  useEffect(() => {
    function handleClickOutside() {
      setActiveDropdownKey(null);
    }
    document.addEventListener("click", handleClickOutside);
    return () => document.removeEventListener("click", handleClickOutside);
  }, []);

  useEffect(() => {
    if (!identifier) return undefined;
    let isMounted = true;

    async function loadData() {
      try {
        const res = await api.get(`/${apiPath}/get?identifier=${encodeURIComponent(identifier)}`);
        if (isMounted) {
          const data = res.data;
          setIdentifierDisplay(data.identifier || data.username || "");

          setAuditInfo({
            createdBy: data.createdBy || null,
            createdOn: data.createdOn || null,
            modifiedBy: data.modifiedBy || null,
            modifiedOn: data.modifiedOn || null,
          });

          const prefilled = {};
          extraFieldsRef.current.forEach((field) => {
            if (data[field.key] !== undefined) {
              prefilled[field.key] = data[field.key];
            }
          });
          setExtraData(prefilled);

          Object.entries(settersRef.current).forEach(([key, setter]) => {
            if (data[key] !== undefined) setter(data[key]);
          });
        }
      } catch (err) {
        if (isMounted) {
          setError(err?.response?.data?.message || "Unable to connect to server.");
        }
      } finally {
        if (isMounted) setLoading(false);
      }
    }
    loadData();

    return () => { isMounted = false; };
  }, [identifier, apiPath]);

  function handleExtraChange(key, value) {
    setExtraData((prev) => ({ ...prev, [key]: value }));
    if (fieldErrors[key]) setFieldErrors((prev) => ({ ...prev, [key]: "" }));
  }

  function handleMultiToggle(key, value) {
    setExtraData((prev) => {
      const current = prev[key] || [];
      const updated = current.includes(value)
        ? current.filter((v) => v !== value)
        : [...current, value];
      return { ...prev, [key]: updated };
    });
    if (fieldErrors[key]) setFieldErrors((prev) => ({ ...prev, [key]: "" }));
  }

  function validate(currentSnapshot) {
    const errors = {};
    extraFields.forEach((field) => {
      if (field.optional) return;
      const val = currentSnapshot[field.key];
      if (field.type === "custom" || field.type === "multiselect") {
        const isEmpty = val === undefined || val === null || val === "" || (Array.isArray(val) && val.length === 0);
        if (isEmpty) errors[field.key] = `${field.label || field.key} is required.`;
        return;
      }
      const isEmpty = val === undefined || val === null || (typeof val === "string" && !val.trim());
      if (isEmpty) errors[field.key] = `${field.label} is required.`;
    });
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSuccess("");
    const dynamicSnapshot = { ...extraData, ...externalExtraData };
    if (!validate(dynamicSnapshot)) return;
    setSubmitting(true);
    try {
      const res = await api.put(`/${apiPath}/update`, {
        identifier: identifierDisplay,
        ...dynamicSnapshot,
      });
      const data = res.data;
      if (data?.identifier || data?.username) {
        setSuccess(`${title} updated successfully`);
        setTimeout(() => router.back(), 1500);
      } else {
        setError("Update failed. Please try again.");
      }
    } catch (err) {
      console.error(err);
      setError(err?.response?.data?.message || "Unable to connect to server.");
    } finally {
      setSubmitting(false);
    }
  }

  const renderFieldInput = (field) => {
    const hasError = !!fieldErrors[field.key];
    const fieldValue = extraData[field.key] ?? "";

    if (field.type === "custom") {
      const ComponentToRender = field.CustomComponent;
      let customComponent;
      if (ComponentToRender) {
        customComponent = (
          <ComponentToRender
            value={fieldValue}
            onChange={(val) => {
              handleExtraChange(field.key, val);
              if (settersRef.current[field.key]) settersRef.current[field.key](val);
            }}
          />
        );
      } else if (typeof field.component === "function") {
        customComponent = field.component({
          isOpen: activeDropdownKey === field.key,
          setOpen: (open) => setActiveDropdownKey(open ? field.key : null),
          value: fieldValue,
          onChange: (val) => {
            handleExtraChange(field.key, val);
            if (settersRef.current[field.key]) settersRef.current[field.key](val);
          },
        });
      } else {
        customComponent = field.component;
      }
      return (
        <section aria-label={field.label}>
          {customComponent}
          {hasError && <span className="text-[11px] text-[#e53e3e] mt-0.5 block">{fieldErrors[field.key]}</span>}
        </section>
      );
    }

    if (field.type === "select") {
      return (
        <>
          <select
            id={`field-${field.key}`}
            className={`py-2 px-3 border-[1.5px] rounded-lg text-sm outline-none bg-[#fafaf8] box-border w-full transition-all focus:border-brand ${hasError ? "border-[#e53e3e]" : "border-gray-300"}`}
            value={fieldValue}
            onChange={(e) => handleExtraChange(field.key, e.target.value)}
          >
            <option value="">Select {field.label}</option>
            {field.options?.map((opt) => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
          {hasError && <span className="text-[11px] text-[#e53e3e] mt-0.5 block">{fieldErrors[field.key]}</span>}
        </>
      );
    }

    if (field.type === "multiselect") {
      const selectedValues = extraData[field.key] || [];
      return (
        <>
          <div
            id={`field-${field.key}`}
            className={`flex flex-wrap gap-2 p-2.5 border-[1.5px] rounded-lg bg-[#fafaf8] min-h-[44px] ${hasError ? "border-[#e53e3e]" : "border-gray-300"}`}
          >
            {field.options?.map((opt) => {
              const isSelected = selectedValues.includes(opt.value);
              return (
                <button
                  key={opt.value}
                  type="button"
                  onClick={() => handleMultiToggle(field.key, opt.value)}
                  className={`py-1 px-3 rounded-2xl border-[1.5px] text-xs md:text-sm cursor-pointer transition-all ${isSelected ? "bg-brand border-brand text-white font-semibold" : "bg-white border-gray-300 text-gray-700 font-medium hover:bg-gray-50"}`}
                >
                  {isSelected ? "✓ " : ""}{opt.label}
                </button>
              );
            })}
          </div>
          {hasError && <span className="text-[11px] text-[#e53e3e] mt-0.5 block">{fieldErrors[field.key]}</span>}
        </>
      );
    }

    return (
      <>
        <input
          id={`field-${field.key}`}
          className={`py-2 px-3 border-[1.5px] rounded-lg text-sm outline-none bg-[#fafaf8] box-border w-full transition-all focus:border-brand ${hasError ? "border-[#e53e3e]" : "border-gray-300"}`}
          type={field.type || "text"}
          placeholder={`Enter ${field.label}`}
          value={fieldValue}
          onChange={(e) => handleExtraChange(field.key, e.target.value)}
        />
        {hasError && <span className="text-[11px] text-[#e53e3e] mt-0.5 block">{fieldErrors[field.key]}</span>}
      </>
    );
  };

  return (
    <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-[#f9fafb] font-sans flex flex-col overflow-hidden">
      <div className="flex-1 p-5 md:p-6 flex flex-col overflow-hidden">
        <div className="flex items-center gap-3 mb-4 shrink-0 relative">
          <button
            className="py-2 px-4 bg-transparent text-brand border-[1.5px] border-brand rounded-lg text-xs md:text-sm font-semibold cursor-pointer shrink-0 transition-colors hover:bg-brand/5"
            onClick={() => router.back()}
            type="button"
          >
            &larr; Back
          </button>
          <h2 className="absolute left-1/2 -translate-x-1/2 m-0 text-xl font-bold text-brand whitespace-nowrap">
            Edit {title}
          </h2>
        </div>

        <div className="flex-1 flex items-center justify-center overflow-hidden">
          <div className="bg-white rounded-xl shadow-[0_2px_10px_rgba(0,0,0,0.06)] p-6 md:p-8 w-full max-w-[720px] max-h-full overflow-auto">
            <p className="text-base md:text-[17px] font-bold text-[#1a1a1a] m-0 mb-1">Update {title}</p>
            <p className="text-xs md:text-sm text-gray-400 mb-5">Update the details below</p>

            {error && (
              <div className="bg-[#fff5f5] border border-[#fca5a5] text-[#c53030] rounded-lg p-2.5 md:p-3.5 text-xs md:text-sm mb-4 text-center col-span-2">
                {error}
              </div>
            )}
            {success && (
              <div className="bg-[#f0fff4] border border-[#9ae6b4] text-[#276749] rounded-lg p-2.5 md:p-3.5 text-xs md:text-sm mb-4 text-center col-span-2">
                {success}
              </div>
            )}

            {loading ? (
              <p className="text-center text-gray-400 text-sm py-10">Loading {title} data&hellip;</p>
            ) : (
              <>
                <form
                  onSubmit={handleSubmit}
                  className="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-3.5"
                >
                  <div className="flex flex-col gap-1.25">
                    <label htmlFor="identifier" className="text-xs md:text-sm font-semibold text-gray-500">
                      Identifier
                    </label>
                    <input
                      id="identifier"
                      className="py-2 px-3 border-[1.5px] border-gray-200 rounded-lg text-sm bg-gray-100 text-gray-400 box-border w-full cursor-not-allowed outline-none"
                      type="text"
                      value={identifierDisplay}
                      disabled
                    />
                  </div>

                  {extraFields.map((field) => (
                    <div key={field.key} className="flex flex-col gap-1.25">
                      {field.type !== "custom" && (
                        <label htmlFor={`field-${field.key}`} className="text-xs md:text-sm font-semibold text-gray-600">
                          {field.label}
                        </label>
                      )}
                      {renderFieldInput(field)}
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
                      className={`flex-1 p-2.5 text-white border-none rounded-lg text-sm font-semibold transition-all ${submitting ? "bg-gray-400 cursor-not-allowed" : "bg-brand cursor-pointer hover:bg-brand-hover"}`}
                      disabled={submitting}
                    >
                      {submitting ? "Saving\u2026" : `Update ${title}`}
                    </button>
                  </div>
                </form>

                <div className="mt-6 pt-5 border-t border-gray-100">
                  <p className="text-[11px] font-semibold text-gray-400 uppercase tracking-wider mb-3">
                    Record Info
                  </p>
                  <div className="grid grid-cols-2 gap-x-6 gap-y-3">
                    <div className="flex flex-col gap-0.5">
                      <span className="text-[11px] font-semibold text-gray-400">Created By</span>
                      <span className="text-[13px] text-gray-700">{auditInfo.createdBy || "—"}</span>
                    </div>
                    <div className="flex flex-col gap-0.5">
                      <span className="text-[11px] font-semibold text-gray-400">Created On</span>
                      <span className="text-[13px] text-gray-700">{formatDateTime(auditInfo.createdOn)}</span>
                    </div>
                    <div className="flex flex-col gap-0.5">
                      <span className="text-[11px] font-semibold text-gray-400">Modified By</span>
                      <span className="text-[13px] text-gray-700">{auditInfo.modifiedBy || "—"}</span>
                    </div>
                    <div className="flex flex-col gap-0.5">
                      <span className="text-[11px] font-semibold text-gray-400">Modified On</span>
                      <span className="text-[13px] text-gray-700">{formatDateTime(auditInfo.modifiedOn)}</span>
                    </div>
                  </div>
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

EditFormSkeleton.propTypes = {
  title: PropTypes.string,
  apiPath: PropTypes.string.isRequired,
  extraFields: PropTypes.arrayOf(
    PropTypes.shape({
      key: PropTypes.string.isRequired,
      label: PropTypes.string,
      type: PropTypes.string,
      optional: PropTypes.bool,
      options: PropTypes.arrayOf(
        PropTypes.shape({
          value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
          label: PropTypes.string.isRequired,
        })
      ),
      component: PropTypes.oneOfType([PropTypes.func, PropTypes.node]),
      CustomComponent: PropTypes.elementType,
    })
  ),
  externalExtraData: PropTypes.objectOf(PropTypes.any),
  setters: PropTypes.objectOf(PropTypes.func),
};
