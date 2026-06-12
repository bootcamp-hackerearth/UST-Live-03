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
  const { get, post } = useApiWithLoader();

  const urlParamValue = params?.[identifierKey] || params?.identifier || "";

  const [identifierDisplay, setIdentifierDisplay] = useState("");
  const [extraData, setExtraData] = useState({});
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
          setError(`No valid identifier provided in URL`);
          setLoading(false);
          return;
        }

        const data = await get(`/${apiPath}/get`, {
          params: { identifier: cleanParamValue },
        });

        if (!isMounted) return;

        const displayVal = data?.username || data?.[identifierKey] || data?.identifier || cleanParamValue;
        setIdentifierDisplay(displayVal);

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
        if (isMounted) {
          setLoading(false);
        }
      }
    }

    if (urlParamValue) {
      loadData();
    }

    return () => {
      isMounted = false;
    };
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

      const res = await post(`/${apiPath}/update`, payload);
      const hasIdentifier = res?.username || res?.[identifierKey] || res?.identifier;

      if (hasIdentifier) {
        setSuccess(`${title} updated successfully`);
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
    const updatedSelection = isSelected
      ? currentSelection.filter((v) => v !== optionValue)
      : [...currentSelection, optionValue];
    
    handleExtraChange(fieldKey, updatedSelection);
  };

  const renderFieldInput = (field) => {
    if (field.type === "custom") return field.component;

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
            const btnClass = isSelected
              ? "bg-[#0097AC] text-white border-[#0097AC]"
              : "bg-white text-[#231F20] border-[#006E74]/30";

            return (
              <button
                key={opt.value}
                type="button"
                onClick={() => handleMultiSelectToggle(field.key, currentSelection, opt.value)}
                className={`px-3 py-1 text-xs rounded-md border transition cursor-pointer ${btnClass}`}
                aria-pressed={isSelected}
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
        id={`field-${field.key}`}
        className="mt-1 w-full px-4 py-2 border rounded-md text-sm border-[#006E74]/30 focus:border-[#006E74] focus:ring-1 focus:ring-[#006E74]"
        type={inputType}
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
          <div className="h-6 w-1/4 bg-[#006E74]/20 rounded"></div>
          <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
            {SKELETON_ITEMS.map((rowKey) => (
              <div key={rowKey} className="h-10 bg-[#006E74]/10 rounded"></div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  const submitBtnClass = submitting ? "bg-[#006E74]/50 cursor-not-allowed" : "bg-[#006E74] hover:bg-[#0097AC] cursor-pointer";
  const submitBtnText = submitting ? "Saving..." : `Update ${title}`;

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

        <div className="sticky bottom-0 bg-white pt-4 border-t border-[#006E74]/20 flex justify-end gap-3">
          <button
            type="button"
            onClick={() => router.back()}
            className="px-6 py-2 text-sm border rounded-md text-[#231F20] border-[#006E74]/30 hover:bg-[#231F20]/5 transition-colors cursor-pointer"
          >
            Cancel
          </button>
          <button type="submit" disabled={submitting} className={`px-6 py-2 text-sm rounded-md text-white transition-colors ${submitBtnClass}`}>
            {submitBtnText}
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
      options: PropTypes.arrayOf(
        PropTypes.shape({
          label: PropTypes.string.isRequired,
          value: PropTypes.any.isRequired,
        })
      ),
      component: PropTypes.node,
    })
  ),
  extraData: PropTypes.object,
  setters: PropTypes.object,
  identifierKey: PropTypes.string,
};