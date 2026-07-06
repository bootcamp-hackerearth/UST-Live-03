"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import api from "@/api/axios";
import { sharedStyles } from "./formSkeletonShared";

const styles = { ...sharedStyles };

const popupStyles = {
  overlay: {
    position: "fixed", inset: 0,
    background: "rgba(0,0,0,0.45)",
    display: "flex", alignItems: "center", justifyContent: "center",
    zIndex: 9999,
  },
  card: {
    background: "#ffffff", borderRadius: "12px",
    padding: "36px 40px", maxWidth: "380px", width: "90%",
    textAlign: "center", boxShadow: "0 8px 32px rgba(0,0,0,0.18)",
  },
  iconCircle: {
    width: "56px", height: "56px", borderRadius: "50%",
    background: "#ffebee", color: "#d32f2f",
    fontSize: "26px", fontWeight: "700",
    display: "flex", alignItems: "center", justifyContent: "center",
    margin: "0 auto 18px",
  },
  title: { fontSize: "18px", fontWeight: "700", color: "#1a1a1a", margin: "0 0 10px" },
  message: { fontSize: "13px", color: "#666666", margin: "0 0 24px", lineHeight: "1.5" },
  okBtn: {
    padding: "10px 40px", background: "#000000", color: "#ffffff",
    border: "none", borderRadius: "7px", fontSize: "13px",
    fontWeight: "600", cursor: "pointer",
  },
};

function isCustomFieldEmpty(val) {
  return val === undefined || val === null || val === "" || (Array.isArray(val) && val.length === 0);
}

function TextField({ fieldKey, label, type, value, hasError, onChange }) {
  const inputId = `field-${fieldKey}`;
  return (
    <>
      <label style={styles.label} htmlFor={inputId}>{label}</label>
      <input
        id={inputId}
        style={{ ...styles.input, ...(hasError ? styles.inputError : {}) }}
        type={type || "text"}
        placeholder={`Enter ${label}`}
        value={value}
        onChange={e => onChange(fieldKey, e.target.value)}
      />
    </>
  );
}

function SelectField({ fieldKey, label, options, hasError, onChange }) {
  const inputId = `field-${fieldKey}`;
  return (
    <>
      <label style={styles.label} htmlFor={inputId}>{label}</label>
      <select
        id={inputId}
        style={{ ...styles.select, ...(hasError ? styles.inputError : {}) }}
        onChange={e => onChange(fieldKey, e.target.value)}
      >
        <option value="">Select {label}</option>
        {options?.map(opt => (
          <option key={opt.value} value={opt.value}>{opt.label}</option>
        ))}
      </select>
    </>
  );
}

function MultiSelectField({ fieldKey, label, options, selected, hasError, onToggle }) {
  const inputId = `field-${fieldKey}`;
  return (
    <>
      <label style={styles.label} htmlFor={inputId}>{label}</label>
      <div
        id={inputId}
        style={{ ...styles.multiWrap, ...(hasError ? styles.inputError : {}) }}
      >
        {options?.map(opt => {
          const isSelected = selected.includes(opt.value);
          return (
            <button
              key={opt.value}
              type="button"
              onClick={() => onToggle(fieldKey, opt.value)}
              style={{ ...styles.chip, ...(isSelected ? styles.chipSelected : {}) }}
            >
              {isSelected ? "✓ " : ""}{opt.label}
            </button>
          );
        })}
      </div>
    </>
  );
}

function renderField({ field, extraData, fieldErrors, activeDropdownKey, setActiveDropdownKey, handleExtraChange, handleMultiToggle }) {
  if (field.type === "custom") {
    return (
      <>
        {typeof field.component === "function"
          ? field.component({ isOpen: activeDropdownKey === field.key, setOpen: open => setActiveDropdownKey(open ? field.key : null) })
          : field.component}
        {fieldErrors[field.key] && <span style={styles.fieldError}>{fieldErrors[field.key]}</span>}
      </>
    );
  }
  if (field.type === "select") {
    return (
      <>
        <SelectField
          fieldKey={field.key}
          label={field.label}
          options={field.options}
          hasError={!!fieldErrors[field.key]}
          onChange={handleExtraChange}
        />
        {fieldErrors[field.key] && <span style={styles.fieldError}>{fieldErrors[field.key]}</span>}
      </>
    );
  }
  if (field.type === "multiselect") {
    return (
      <>
        <MultiSelectField
          fieldKey={field.key}
          label={field.label}
          options={field.options}
          selected={extraData[field.key] || []}
          hasError={!!fieldErrors[field.key]}
          onToggle={handleMultiToggle}
        />
        {fieldErrors[field.key] && <span style={styles.fieldError}>{fieldErrors[field.key]}</span>}
      </>
    );
  }
  return (
    <>
      <TextField
        fieldKey={field.key}
        label={field.label}
        type={field.type}
        value={extraData[field.key] || ""}
        hasError={!!fieldErrors[field.key]}
        onChange={handleExtraChange}
      />
      {fieldErrors[field.key] && <span style={styles.fieldError}>{fieldErrors[field.key]}</span>}
    </>
  );
}

export default function AddFormSkeleton({
  title, apiPath,
  apiEndpoint = "add",
  extraFields = [],
  extraData: externalExtraData = {},
  showIdentifier = true,
  successKeys = [],
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
  const [accessDenied, setAccessDenied] = useState(false);

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

  function validateField(field, errors) {
    if (field.type === "custom") {
      if (field.optional) return;
      const val = externalExtraData[field.key];
      if (isCustomFieldEmpty(val))
        errors[field.key] = `${field.label || field.key} is required.`;
    } else if (field.type === "multiselect") {
      if ((extraData[field.key] || []).length === 0) errors[field.key] = `${field.label} is required.`;
    } else {
      const val = String(extraData[field.key] || "").trim();
      if (!val) {
        errors[field.key] = `${field.label} is required.`;
      } else if (field.validate) {
        const msg = field.validate(val);
        if (msg) errors[field.key] = msg;
      }
    }
  }

  function validate() {
    const errors = {};
    if (showIdentifier && !identifier.trim()) errors.identifier = "Identifier is required.";
    extraFields.forEach(field => validateField(field, errors));
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
      }, { skipErrorRedirect: [403] });
      const data = res.data;
      if (data.success === false) {
        setError(data.message || "Already exists. Please use a different one.");
        setIdentifierError(true);
        return;
      }
      const allSuccessKeys = ["identifier", "username", ...successKeys];
      if (allSuccessKeys.some(key => data[key])) {
        setSuccess(`${title} added successfully`);
        setTimeout(() => router.back(), 1500);
      } else {
        setError("Failed to add. Please try again.");
      }
    } catch (err) {
      if (err.response?.status === 403) {
        setAccessDenied(true);
      } else {
        setError("Unable to connect to server. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
    {accessDenied && (
      <div style={popupStyles.overlay}>
        <div style={popupStyles.card}>
          <div style={popupStyles.iconCircle}>!</div>
          <h3 style={popupStyles.title}>Access Denied</h3>
          <p style={popupStyles.message}>
            You do not have permission to perform this action.
          </p>
          <button
            style={popupStyles.okBtn}
            onClick={() => router.push(`/${apiPath}/list`)}
          >
            OK
          </button>
        </div>
      </div>
    )}
    <div style={styles.page}>
      <div style={styles.inner}>
        <div style={styles.topRow}>
          <button style={styles.backBtn} onClick={() => router.back()}>⮜ Back</button>
          <h2 style={styles.pageTitle}>Add {title}</h2>
        </div>

        <div style={styles.cardWrap}>
          <div style={styles.card}>
            <p style={styles.cardTitle}>New {title}</p>
            <p style={styles.cardSubtitle}>Fill in the details below</p>

            {error   && <div style={styles.errorBox}>{error}</div>}
            {success && <div style={styles.successBox}>{success}</div>}

            <form onSubmit={handleSubmit} style={styles.form}>
              {showIdentifier && (
                <div style={styles.field}>
                  <label style={styles.label} htmlFor="field-identifier">Identifier</label>
                  <input
                    id="field-identifier"
                    style={{ ...styles.input, ...((identifierError || fieldErrors.identifier) ? styles.inputError : {}) }}
                    type="text" placeholder="Enter identifier"
                    value={identifier} onChange={handleIdentifierChange}
                  />
                  {fieldErrors.identifier && <span style={styles.fieldError}>{fieldErrors.identifier}</span>}
                </div>
              )}

              {extraFields.map(field => (
                <div key={field.key} style={styles.field}>
                  {renderField({
                    field, extraData, fieldErrors,
                    activeDropdownKey, setActiveDropdownKey,
                    handleExtraChange, handleMultiToggle,
                  })}
                </div>
              ))}

              <div style={styles.buttonRow}>
                <button type="button" style={styles.cancelBtn} onClick={() => router.back()}>Cancel</button>
                <button
                  type="submit"
                  style={{ ...styles.submitBtn, ...(loading ? styles.submitBtnDisabled : {}) }}
                  disabled={loading}
                >
                  {loading ? "Saving…" : `Add ${title}`}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    </>
  );
}

AddFormSkeleton.propTypes = {
  title: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  apiEndpoint: PropTypes.string,
  extraFields: PropTypes.arrayOf(PropTypes.shape({
    key: PropTypes.string.isRequired,
    label: PropTypes.string,
    type: PropTypes.string,
    options: PropTypes.arrayOf(PropTypes.shape({
      value: PropTypes.string,
      label: PropTypes.string,
    })),
    component: PropTypes.oneOfType([PropTypes.node, PropTypes.func]),
  })),
  extraData: PropTypes.object,
  showIdentifier: PropTypes.bool,
  successKeys: PropTypes.arrayOf(PropTypes.string),
};

TextField.propTypes = {
  fieldKey: PropTypes.string.isRequired,
  label: PropTypes.string,
  type: PropTypes.string,
  value: PropTypes.string,
  hasError: PropTypes.bool,
  onChange: PropTypes.func.isRequired,
};

SelectField.propTypes = {
  fieldKey: PropTypes.string.isRequired,
  label: PropTypes.string,
  options: PropTypes.array,
  hasError: PropTypes.bool,
  onChange: PropTypes.func.isRequired,
};

MultiSelectField.propTypes = {
  fieldKey: PropTypes.string.isRequired,
  label: PropTypes.string,
  options: PropTypes.array,
  selected: PropTypes.array.isRequired,
  hasError: PropTypes.bool,
  onToggle: PropTypes.func.isRequired,
};