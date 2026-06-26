// components/edit/BaseEditForm.jsx

"use client";

import { useEffect, useState, useRef } from "react";
import { useRouter, useParams } from "next/navigation";
import PropTypes from "prop-types";
import { useApiWithLoader } from "../../app/lib/useApiWithLoader";

const SKELETON_ITEMS = [
  "skeleton-row-0",
  "skeleton-row-1",
  "skeleton-row-2",
  "skeleton-row-3",
  "skeleton-row-4",
  "skeleton-row-5"
];

function formatDateTime(value) {
  if (!value) return "—";
  try {
    if (Array.isArray(value)) {
      const [y, mo, d, h = 0, mi = 0, s = 0] = value;
      return new Date(y, mo - 1, d, h, mi, s).toLocaleString("en-IN", {
        dateStyle: "medium",
        timeStyle: "short",
      });
    }
    return new Date(value).toLocaleString("en-IN", {
      dateStyle: "medium",
      timeStyle: "short",
    });
  } catch {
    return String(value);
  }
}

function AuditTrail({ createdBy, createdAt, modifiedBy, modifiedAt }) {
  const hasAnyData = createdBy || createdAt || modifiedBy || modifiedAt;
  if (!hasAnyData) return null;

  return (
    <div className="mt-8 border-t border-[#006E74]/10 pt-6">
      <div className="flex items-center gap-2 mb-4">
        <span className="text-[10px] font-bold text-[#006E74] uppercase tracking-widest">
          Audit Trail
        </span>
        <div className="flex-1 h-px bg-[#006E74]/10" />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="flex items-start gap-3 bg-[#006E74]/4 rounded-xl px-4 py-3 border border-[#006E74]/10">
          <div className="w-7 h-7 rounded-lg bg-[#006E74]/10 flex items-center justify-center shrink-0 mt-0.5">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="#006E74" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" />
            </svg>
          </div>
          <div className="min-w-0">
            <p className="text-[9px] font-bold text-[#006E74]/60 uppercase tracking-widest">Created</p>
            <p className="text-xs font-semibold text-[#231F20] mt-0.5 truncate">
              {createdBy || "—"}
            </p>
            <p className="text-[10px] text-gray-400 font-mono mt-0.5">
              {formatDateTime(createdAt)}
            </p>
          </div>
        </div>

        <div className="flex items-start gap-3 bg-gray-50 rounded-xl px-4 py-3 border border-gray-100">
          <div className="w-7 h-7 rounded-lg bg-gray-200 flex items-center justify-center shrink-0 mt-0.5">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="#6B7280" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" /><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
            </svg>
          </div>
          <div className="min-w-0">
            <p className="text-[9px] font-bold text-gray-400 uppercase tracking-widest">Last modified</p>
            <p className="text-xs font-semibold text-[#231F20] mt-0.5 truncate">
              {modifiedBy || "Not yet modified"}
            </p>
            <p className="text-[10px] text-gray-400 font-mono mt-0.5">
              {formatDateTime(modifiedAt)}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

AuditTrail.propTypes = {
  createdBy: PropTypes.string,
  createdAt: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.arrayOf(PropTypes.number),
    PropTypes.instanceOf(Date),
  ]),
  modifiedBy: PropTypes.string,
  modifiedAt: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.arrayOf(PropTypes.number),
    PropTypes.instanceOf(Date),
  ]),
};

export default function BaseEditForm({
  title,
  apiPath,
  extraFields = [],
  extraData: externalExtraData = {},
  setters = {},
  identifierKey = "identifier",
}) {
  const router = useRouter();
  const params = useParams();
  const { get, put } = useApiWithLoader();

  const urlParamValue = params?.[identifierKey] || params?.identifier || "";

  const [identifierDisplay, setIdentifierDisplay] = useState("");
  const [extraData, setExtraData] = useState({});
  const [audit, setAudit] = useState({
    createdBy: null,
    createdAt: null,
    modifiedBy: null,
    modifiedAt: null,
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const displayLabel = identifierKey.charAt(0).toUpperCase() + identifierKey.slice(1);

  const fieldsRef = useRef(extraFields);
  const settersRef = useRef(setters);

  useEffect(() => {
    fieldsRef.current = extraFields;
    settersRef.current = setters;
  }, [extraFields, setters]);

  useEffect(() => {
    let isMounted = true;

    async function loadData() {
      try {
        setLoading(true);
        setError("");

        const cleanParamValue = urlParamValue
          ? decodeURIComponent(decodeURIComponent(urlParamValue))
          : "";

        if (!cleanParamValue) {
          setError("No valid identifier provided in URL");
          setLoading(false);
          return;
        }

        const data = await get(`/${apiPath}/get`, {
          params: { identifier: cleanParamValue },
        });

        if (!isMounted) return;

        const displayVal =
          data?.username || data?.[identifierKey] || data?.identifier || cleanParamValue;
        setIdentifierDisplay(displayVal);

        setAudit({
          createdBy: data?.createdBy ?? null,
          createdAt: data?.createdAt ?? null,
          modifiedBy: data?.modifiedBy ?? null,
          modifiedAt: data?.modifiedAt ?? null,
        });

        const prefilled = {};
        fieldsRef.current.forEach((field) => {
          if (data?.[field.key] !== undefined) {
            prefilled[field.key] = data[field.key];
          }
        });
        setExtraData(prefilled);

        Object.entries(settersRef.current).forEach(([key, setter]) => {
          if (data?.[key] !== undefined && typeof setter === "function") {
            setter(data[key]);
          }
        });
      } catch (err) {
        if (isMounted) {
          const errorMsg = err?.response?.data?.message || "Could not load form data";
          setError(errorMsg);
        }
      } finally {
        if (isMounted) setLoading(false);
      }
    }

    if (urlParamValue) loadData();
    return () => { isMounted = false; };
  }, [urlParamValue, apiPath, identifierKey, get]);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSuccess("");
    setSubmitting(true);

    try {
      const payload = {
        identifier: identifierDisplay,
        ...extraData,
        ...externalExtraData,
      };

      const res = await put(`/${apiPath}/update`, payload);
      const hasIdentifier = res?.username || res?.[identifierKey] || res?.identifier;

      if (hasIdentifier) {
        setSuccess(`${title} updated successfully`);
        setAudit((prev) => ({
          ...prev,
          modifiedBy: res?.modifiedBy ?? prev.modifiedBy,
          modifiedAt: res?.modifiedAt ?? prev.modifiedAt,
        }));
        setTimeout(() => router.back(), 1500);
      } else {
        setError("Update failed. Please try again.");
      }
    } catch (err) {
      const errorMsg = err?.response?.data?.message || "Unable to connect to server";
      setError(errorMsg);
    } finally {
      setSubmitting(false);
    }
  }

  const handleExtraChange = (key, value) => {
    setExtraData((prev) => ({ ...prev, [key]: value }));
    if (settersRef.current[key] && typeof settersRef.current[key] === "function") {
      settersRef.current[key](value);
    }
  };

  const handleMultiSelectToggle = (fieldKey, currentSelection, optionValue) => {
    const isSelected = currentSelection.includes(optionValue);
    const updated = isSelected
      ? currentSelection.filter((v) => v !== optionValue)
      : [...currentSelection, optionValue];
    handleExtraChange(fieldKey, updated);
  };

  const renderFieldInput = (field) => {
    if (field.type === "custom") return typeof field.render === "function" ? field.render() : field.component;

    if (field.type === "select") {
      return (
        <select
          id={`field-${field.key}`}
          className="mt-1 w-full px-4 py-2 border rounded-md text-sm border-[#006E74]/30 focus:border-[#006E74] focus:ring-1 focus:ring-[#006E74] bg-white cursor-pointer"
          value={extraData[field.key] || ""}
          onChange={(e) => handleExtraChange(field.key, e.target.value)}
          aria-label={field.label}
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
            const currentSelection = extraData[field.key] || [];
            const isSelected = currentSelection.includes(opt.value);
            return (
              <button
                key={opt.value}
                type="button"
                onClick={() => handleMultiSelectToggle(field.key, currentSelection, opt.value)}
                className={`px-3 py-1 text-xs rounded-md border transition cursor-pointer ${isSelected
                    ? "bg-[#0097AC] text-white border-[#0097AC]"
                    : "bg-white text-[#231F20] border-[#006E74]/30"
                  }`}
                aria-pressed={isSelected}
              >
                {opt.label}
              </button>
            );
          })}
        </div>
      );
    }

    return (
      <input
        id={`field-${field.key}`}
        className="mt-1 w-full px-4 py-2 border rounded-md text-sm border-[#006E74]/30 focus:border-[#006E74] focus:ring-1 focus:ring-[#006E74]"
        type={field.type || "text"}
        value={extraData[field.key] || ""}
        placeholder={`Enter ${field.label?.toLowerCase() || field.key}`}
        onChange={(e) => handleExtraChange(field.key, e.target.value)}
        aria-label={field.label}
      />
    );
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-white p-6">
        <div className="animate-pulse space-y-4">
          <div className="h-6 w-1/4 bg-[#006E74]/20 rounded" />
          <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
            {SKELETON_ITEMS.map((k) => (
              <div key={k} className="h-10 bg-[#006E74]/10 rounded" />
            ))}
          </div>
          {/* Audit skeleton */}
          <div className="mt-8 border-t border-[#006E74]/10 pt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="h-16 bg-[#006E74]/6 rounded-xl" />
            <div className="h-16 bg-gray-100 rounded-xl" />
          </div>
        </div>
      </div>
    );
  }

  const submitBtnClass = submitting
    ? "bg-[#006E74]/50 cursor-not-allowed"
    : "bg-[#006E74] hover:bg-[#0097AC] cursor-pointer";

  return (
    <div className="min-h-screen bg-white p-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-[#231F20]">Edit {title}</h1>
        <p className="text-sm text-[#0097AC] mt-1">Update configuration details</p>
      </div>

      {error && (
        <div role="alert" className="mb-4 px-4 py-3 border text-sm rounded-md bg-white text-red-600 border-red-300">
          {error}
        </div>
      )}
      {success && (
        <output className="block mb-4 px-4 py-3 border text-sm rounded-md bg-white text-green-600 border-green-300">
          {success}
        </output>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
          <div>
            <label htmlFor="id-field" className="text-xs font-semibold text-[#006E74] uppercase">
              {displayLabel} (Username)
            </label>
            <input
              id="id-field"
              className="mt-1 w-full px-4 py-2 border rounded-md text-sm bg-gray-100 text-gray-500 border-[#006E74]/20 cursor-not-allowed"
              type="text"
              value={identifierDisplay}
              disabled
              aria-readonly="true"
            />
          </div>

          {extraFields.map((field) => (
            <div key={field.key}>
              {field.type !== "custom" && (
                <label htmlFor={`field-${field.key}`} className="text-xs font-semibold text-[#006E74] uppercase">
                  {field.label || field.key}
                </label>
              )}
              <div>{renderFieldInput(field)}</div>
            </div>
          ))}
        </div>

        <AuditTrail
          createdBy={audit.createdBy}
          createdAt={audit.createdAt}
          modifiedBy={audit.modifiedBy}
          modifiedAt={audit.modifiedAt}
        />

        <div className="sticky bottom-0 bg-white pt-4 border-t border-[#006E74]/20 flex justify-end gap-3">
          <button
            type="button"
            onClick={() => router.back()}
            className="px-6 py-2 text-sm border rounded-md text-[#231F20] border-[#006E74]/30 hover:bg-[#231F20]/5 transition-colors cursor-pointer"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={submitting}
            className={`px-6 py-2 text-sm rounded-md text-white transition-colors ${submitBtnClass}`}
          >
            {submitting ? "Saving..." : `Update ${title}`}
          </button>
        </div>
      </form>
    </div>
  );
}

BaseEditForm.propTypes = {
  title: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  extraFields: PropTypes.arrayOf(
    PropTypes.shape({
      key: PropTypes.string.isRequired,
      label: PropTypes.string,
      type: PropTypes.string,
      options: PropTypes.arrayOf(PropTypes.shape({ label: PropTypes.string.isRequired, value: PropTypes.any.isRequired })),
      component: PropTypes.node,
      render: PropTypes.func,
    })
  ),
  extraData: PropTypes.object,
  setters: PropTypes.object,
  identifierKey: PropTypes.string,
};