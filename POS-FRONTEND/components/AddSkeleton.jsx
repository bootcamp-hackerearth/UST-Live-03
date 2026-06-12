"use client";

import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import api from "@/api/axios";

const C = {
  navy: "#363955", mid: "#54668E", light: "#879EC6",
  gray: "#E8E8E8", offWhite: "#F5F6E6", text: "#1e2235",
  muted: "#6b7280", white: "#ffffff",
  error: "#c0392b", errorBg: "#fdf2f2",
};

export default function AddFormSkeleton({
  title, apiPath,
  apiEndpoint = "add",
  extraFields = [],
  extraData: externalExtraData = {},
  showIdentifier = true,
  validateIdentifier = false,
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

  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  useEffect(() => {
    const handleToggle = (e) => setIsSidebarOpen(e.detail.isOpen);
    globalThis.addEventListener("sidebar-toggle", handleToggle);
    return () => globalThis.removeEventListener("sidebar-toggle", handleToggle);
  }, []);

  function handleExtraChange(key, value) {
    setExtraData(prev => ({ ...prev, [key]: value }));
    if (fieldErrors[key]) setFieldErrors(prev => ({ ...prev, [key]: "" }));
  }

  function handleMultiToggle(key, value) {
    setExtraData(prev => {
      const current = prev[key] || [];
      const updated = current.includes(value) ? current.filter(v => v !== value) : [...current, value];
      return { ...prev, [key]: updated };
    });
    if (fieldErrors[key]) setFieldErrors(prev => ({ ...prev, [key]: "" }));
  }

  function handleIdentifierChange(e) {
    setIdentifier(e.target.value);
    if (identifierError) setIdentifierError(false);
    if (error) setError("");
    if (fieldErrors.identifier) setFieldErrors(prev => ({ ...prev, identifier: "" }));
  }

  function validate() {
    const errors = {};

    if (showIdentifier) {
      const idVal = identifier.trim();
      if (!idVal) {
        errors.identifier = "Identifier is required.";
      } else if (validateIdentifier) {
        const emailRegex = /^[^@\s]+@[^@\s]+\.[^@\s]+$/; // NOSONAR
        if (!emailRegex.test(idVal))
          errors.identifier = "Enter a valid email address (e.g. user@gmail.com).";
      }
    }

    extraFields.forEach(field => {
      if (field.type === "custom") return;
      if (field.type === "multiselect") {
        if ((extraData[field.key] || []).length === 0)
          errors[field.key] = `${field.label} is required.`;
      } else {
        const val = String(extraData[field.key] || "").trim();
        if (!val) {
          errors[field.key] = `${field.label} is required.`;
        } else if (field.validate) {
          const msg = field.validate(val);
          if (msg) errors[field.key] = msg;
        }
      }
    });

    extraFields.forEach(field => {
      if (field.type !== "custom") return;
      if (field.optional) return;
      const val = externalExtraData[field.key];
      if (val === undefined || val === null || val === "" || (Array.isArray(val) && val.length === 0))
        errors[field.key] = `${field.label || field.key} is required.`;
    });

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError(""); setSuccess(""); setIdentifierError(false);
    if (!validate()) return;
    setLoading(true);
    try {
      const res = await api.post(`/${apiPath}/${apiEndpoint}`, {
        ...(showIdentifier ? { identifier } : {}),
        ...extraData,
        ...externalExtraData,
      });
      const data = res.data;
      if (data.success === false) {
        setError(data.message || "Already exists. Please use a different one.");
        setIdentifierError(true);
        return;
      }
      if (data.identifier || data.username) {
        setSuccess(`${title} added successfully`);
        setTimeout(() => router.back(), 1500);
      } else {
        setError("Failed to add. Please try again.");
      }
    } catch (err) {
      if (process.env.NODE_ENV !== "production") console.error(err);
      setError("Unable to connect to server. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  const allFields = [
    ...(showIdentifier ? [{ key: "__identifier__", label: "Identifier", type: "text", _isIdentifier: true }] : []),
    ...extraFields,
  ];

  let statusAlert = null;

  if (error) {
    statusAlert = { text: error, bg: C.errorBg, border: "#f5c6c6", color: C.error };
  } else if (success) {
    statusAlert = { text: success, bg: "#f0fdf4", border: "#86efac", color: "#166534" };
  }

  return (
    <div style={{
      position: "fixed", top: "60px", right: 0, bottom: 0,
      left: isSidebarOpen ? "220px" : "55px",
      backgroundColor: "#ffffff",
      fontFamily: "'Segoe UI', sans-serif",
      display: "flex", flexDirection: "column",
      overflow: "hidden", transition: "left 0.2s ease",
    }}>

      <div style={{
        background: "#ffffff",
        padding: "16px 28px",
        display: "flex", alignItems: "center", gap: "14px",
        flexShrink: 0,
        borderBottom: "1.5px solid #e8eaf0",
      }}>
        <button
          type="button"
          onClick={() => router.back()}
          style={{
            background: "#ffffff",
            border: `1.5px solid ${C.mid}`,
            color: C.mid, borderRadius: "7px",
            padding: "5px 14px", fontSize: "12px",
            fontWeight: "600", cursor: "pointer",
          }}
        >
          ← Back
        </button>
        <div style={{ width: "1px", height: "22px", background: "#e8eaf0" }} />
        <h2 style={{
          margin: 0, fontSize: "18px", fontWeight: "700",
          color: C.navy, letterSpacing: "0.1px",
        }}>
          Add {title}
        </h2>
      </div>

      <div style={{
        flex: 1, overflow: "auto",
        padding: "28px 32px",
        display: "flex", alignItems: "flex-start", justifyContent: "center",
      }}>
        <div style={{
          width: "100%", maxWidth: "1100px",
          background: C.white, borderRadius: "12px",
          boxShadow: "0 1px 12px rgba(54,57,85,0.07)",
          border: "1.5px solid #e8eaf0",
        }}>

          <div style={{
            padding: "18px 28px 16px",
            borderBottom: "1.5px solid #e8eaf0",
            display: "flex", alignItems: "center",
            justifyContent: "space-between",
          }}>
            <div>
              <p style={{ margin: 0, fontSize: "15px", fontWeight: "700", color: C.navy }}>
                New {title}
              </p>
              <p style={{ margin: "3px 0 0", fontSize: "12px", color: C.muted }}>
                Fill in all required fields below
              </p>
            </div>
            <div style={{
              background: "#EEF0F8", borderRadius: "8px",
              padding: "5px 14px", fontSize: "12px",
              fontWeight: "600", color: C.mid,
              border: "1px solid #d8dce8",
            }}>
              + New Record
            </div>
          </div>

          {statusAlert && (
            <div style={{ padding: "12px 28px 0" }}>
              <div style={{
                background: statusAlert.bg,
                border: `1px solid ${statusAlert.border}`,
                color: statusAlert.color,
                borderRadius: "7px",
                padding: "9px 14px",
                fontSize: "13px",
              }}>
                {statusAlert.text}
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit} style={{ padding: "20px 28px 24px" }}>
            <div style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fill, minmax(260px, 1fr))",
              gap: "16px 20px",
            }}>
              {allFields.map(field => {
                const isIdentifier = field._isIdentifier;
                const hasError = isIdentifier
                  ? (identifierError || !!fieldErrors.identifier)
                  : !!fieldErrors[field.key];
                return (
                  <FieldRenderer
                    key={field.key}
                    field={field}
                    isIdentifier={isIdentifier}
                    hasError={hasError}
                    identifier={identifier}
                    identifierError={identifierError}
                    fieldErrors={fieldErrors}
                    extraData={extraData}
                    activeDropdownKey={activeDropdownKey}
                    setActiveDropdownKey={setActiveDropdownKey}
                    handleIdentifierChange={handleIdentifierChange}
                    handleExtraChange={handleExtraChange}
                    handleMultiToggle={handleMultiToggle}
                  />
                );
              })}
            </div>

            <div style={{
              display: "flex", justifyContent: "flex-end",
              gap: "10px", marginTop: "24px",
              padding: "18px", borderTop: "1.5px solid #e8eaf0",
            }}>
              <button
                type="button"
                onClick={() => router.back()}
                style={{
                  padding: "9px 24px", borderRadius: "7px",
                  border: "1.5px solid #e8eaf0",
                  background: "#ffffff", color: "#374151",
                  fontSize: "13px", fontWeight: "600", cursor: "pointer",
                }}
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={loading}
                style={{
                  padding: "9px 28px", borderRadius: "7px", border: "none",
                  background: loading
                    ? "#c4c8d4"
                    : `linear-gradient(135deg, ${C.navy}, ${C.mid})`,
                  color: "#fff", fontSize: "13px",
                  fontWeight: "600", cursor: loading ? "not-allowed" : "pointer",
                  boxShadow: loading ? "none" : "0 3px 10px rgba(54,57,85,0.25)",
                }}
              >
                {loading ? "Saving…" : `Add ${title}`}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

const inputStyle = {
  padding: "9px 12px",
  borderRadius: "7px",
  fontSize: "13px",
  outline: "none",
  backgroundColor: "#fafafa",
  boxSizing: "border-box",
  width: "100%",
  color: "#1e2235",
  borderWidth: "1.5px",
  borderStyle: "solid",
  borderColor: "#E8E8E8",
};

const inputErrorStyle = {
  borderColor: "#c0392b",
  backgroundColor: "#fdf2f2",
};

const errText = {
  fontSize: "11px", color: "#c0392b", marginTop: "2px",
};

AddFormSkeleton.propTypes = {
  title: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  apiEndpoint: PropTypes.string,
  extraFields: PropTypes.array,
  extraData: PropTypes.object,
  showIdentifier: PropTypes.bool,
  validateIdentifier: PropTypes.bool,
};

function renderIdentifierField({ hasError, identifier, fieldErrors, handleIdentifierChange }) {
  return (
    <>
      <input
        id="__identifier__"
        style={{ ...inputStyle, ...(hasError ? inputErrorStyle : {}) }}
        type="text"
        placeholder="Enter identifier"
        value={identifier}
        onChange={handleIdentifierChange}
      />
      {fieldErrors.identifier && <span style={errText}>{fieldErrors.identifier}</span>}
    </>
  );
}

function renderCustomField({ field, activeDropdownKey, setActiveDropdownKey, fieldErrors }) {
  return (
    <>
      {typeof field.component === "function"
        ? field.component({ isOpen: activeDropdownKey === field.key, setOpen: open => setActiveDropdownKey(open ? field.key : null) })
        : field.component}
      {fieldErrors[field.key] && <span style={errText}>{fieldErrors[field.key]}</span>}
    </>
  );
}

function renderSelectField({ field, hasError, handleExtraChange, fieldErrors }) {
  return (
    <>
      <select
        id={field.key}
        style={{ ...inputStyle, ...(hasError ? inputErrorStyle : {}) }}
        onChange={e => handleExtraChange(field.key, e.target.value)}
      >
        <option value="">Select {field.label}</option>
        {field.options?.map(opt => (
          <option key={opt.value} value={opt.value}>{opt.label}</option>
        ))}
      </select>
      {fieldErrors[field.key] && <span style={errText}>{fieldErrors[field.key]}</span>}
    </>
  );
}

function renderMultiSelectField({ field, hasError, extraData, handleMultiToggle, fieldErrors }) {
  const inner = (
    <div style={{
      display: "flex", flexWrap: "wrap", gap: "6px",
      padding: "8px",
      borderRadius: "7px", backgroundColor: "#fafafa", minHeight: "42px",
      borderWidth: "1.5px",
      borderStyle: "solid",
      borderColor: hasError ? C.error : C.gray,
    }}>
      {field.options?.map(opt => {
        const isSelected = (extraData[field.key] || []).includes(opt.value);
        return (
          <button
            key={opt.value}
            type="button"
            onClick={() => handleMultiToggle(field.key, opt.value)}
            style={{
              padding: "3px 11px", borderRadius: "20px",
              fontSize: "12px", fontWeight: isSelected ? "600" : "500",
              cursor: "pointer",
              border: `1.5px solid ${isSelected ? C.mid : C.gray}`,
              background: isSelected
                ? `linear-gradient(135deg, ${C.navy}, ${C.mid})`
                : C.white,
              color: isSelected ? "#fff" : "#374151",
            }}
          >
            {isSelected ? "✓ " : ""}{opt.label}
          </button>
        );
      })}
    </div>
  );

  return (
    <>
      {inner}
      {fieldErrors[field.key] && <span style={errText}>{fieldErrors[field.key]}</span>}
    </>
  );
}

function renderDefaultField({ field, hasError, handleExtraChange, fieldErrors }) {
  return (
    <>
      <input
        id={field.key}
        style={{ ...inputStyle, ...(hasError ? inputErrorStyle : {}) }}
        type={field.type || "text"}
        placeholder={`Enter ${field.label || ""}`}
        onChange={e => handleExtraChange(field.key, e.target.value)}
      />
      {fieldErrors[field.key] && <span style={errText}>{fieldErrors[field.key]}</span>}
    </>
  );
}

function FieldRenderer(props) {
  const {
    field,
    isIdentifier,
    hasError,
    identifier,
    fieldErrors,
    extraData,
    activeDropdownKey,
    setActiveDropdownKey,
    handleIdentifierChange,
    handleExtraChange,
    handleMultiToggle,
  } = props;

  let fieldElement;

  if (isIdentifier) {
    fieldElement = renderIdentifierField({ hasError, identifier, fieldErrors, handleIdentifierChange });
  } else if (field.type === "custom") {
    fieldElement = renderCustomField({ field, activeDropdownKey, setActiveDropdownKey, fieldErrors });
  } else if (field.type === "select") {
    fieldElement = renderSelectField({ field, hasError, handleExtraChange, fieldErrors });
  } else if (field.type === "multiselect") {
    fieldElement = renderMultiSelectField({ field, hasError, extraData, handleMultiToggle, fieldErrors });
  } else {
    fieldElement = renderDefaultField({ field, hasError, handleExtraChange, fieldErrors });
  }

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "5px" }}>
      {field.type !== "custom" && (
        <label
          htmlFor={isIdentifier ? "__identifier__" : field.key}
          style={{
            fontSize: "11px",
            fontWeight: "700",
            color: "#4b5563",
            letterSpacing: "0.4px",
            textTransform: "uppercase",
          }}
        >
          {field.label}
        </label>
      )}

      {fieldElement}
    </div>
  );
}

FieldRenderer.propTypes = {
  field: PropTypes.object.isRequired,
  isIdentifier: PropTypes.bool,
  hasError: PropTypes.bool,
  identifier: PropTypes.string,
  fieldErrors: PropTypes.object,
  extraData: PropTypes.object,
  activeDropdownKey: PropTypes.any,
  setActiveDropdownKey: PropTypes.func,
  handleIdentifierChange: PropTypes.func.isRequired,
  handleExtraChange: PropTypes.func.isRequired,
  handleMultiToggle: PropTypes.func.isRequired,
};