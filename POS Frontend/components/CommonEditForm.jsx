"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import PropTypes from "prop-types";
import api from "@/api/axios";
import { C, sharedStyles } from "./formSkeletonShared";

const styles = {
  ...sharedStyles,
  inputDisabled: {
    padding: "9px 12px", border: `1.5px solid ${C.gray}`,
    borderRadius: "7px", fontSize: "13px",
    background: C.offWhite, color: "#9ca3af",
    boxSizing: "border-box", width: "100%",
    cursor: "not-allowed", outline: "none",
  },
  loadingText: { textAlign: "center", color: C.muted, fontSize: "14px", padding: "40px 0" },
};

function clearFieldError(key, setFieldErrors) {
  setFieldErrors(prev => ({ ...prev, [key]: "" }));
}

export default function EditFormSkeleton({
  title, apiPath,
  paramName = "identifier", identifierField = "identifier", getStyle = "path",
  extraFields = [], extraData: externalExtraData = {}, setters = {}, overrideIdentifier = "",
}) {
  const router = useRouter();
  const params = useParams();
  const identifier = overrideIdentifier || decodeURIComponent(params[paramName] || "");

  const [identifierDisplay, setIdentifierDisplay] = useState("");
  const [recordId, setRecordId]   = useState(null);
  const [extraData, setExtraData] = useState({});
  const [error, setError]         = useState("");
  const [success, setSuccess]     = useState("");
  const [loading, setLoading]     = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});

  useEffect(() => {
    if (!identifier) return;
    async function loadData() {
      try {
        const res = await api.get(`/${apiPath}/get`, { params: { [identifierField]: identifier } });
        const data = res.data;
        setIdentifierDisplay(data[identifierField]);
        setRecordId(data.id);
        const prefilled = {};
        extraFields.forEach(field => {
          if (field.type !== "custom" && data[field.key] !== undefined) prefilled[field.key] = data[field.key];
        });
        setExtraData(prefilled);
        Object.entries(setters).forEach(([key, setter]) => { if (data[key] !== undefined) setter(data[key]); });
      } catch {
        setError("Could not load data. Please go back and try again.");
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, [identifier, apiPath]);

  function handleExtraChange(key, value) {
    setExtraData(prev => ({ ...prev, [key]: value }));
    if (fieldErrors[key]) clearFieldError(key, setFieldErrors);
  }

  function handleMultiToggle(key, value) {
    setExtraData(prev => {
      const current = prev[key] || [];
      const updated = current.includes(value)
        ? current.filter(v => v !== value)
        : [...current, value];
      return { ...prev, [key]: updated };
    });
    if (fieldErrors[key]) clearFieldError(key, setFieldErrors);
  }

  function validate() {
    const errors = {};
    extraFields.forEach(field => {
      if (field.type === "custom") {
        const val = externalExtraData[field.key];
        if (val === undefined || val === null || val === "" || (Array.isArray(val) && val.length === 0))
          errors[field.key] = `${field.label || field.key} is required.`;
        return;
      }
      if (field.type === "multiselect") {
        if ((extraData[field.key] || []).length === 0) errors[field.key] = `${field.label} is required.`;
        return;
      }
      if (!String(extraData[field.key] || "").trim()) errors[field.key] = `${field.label} is required.`;
    });
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError(""); setSuccess("");
    if (!validate()) return;
    setSubmitting(true);
    try {
      const res = await api.post(`/${apiPath}/update`, {
        id: recordId, [identifierField]: identifierDisplay,
        ...extraData, ...externalExtraData,
      });
      const data = res.data;
      if (data?.[identifierField]) {
        setSuccess(`${title} updated successfully`);
        setTimeout(() => router.back(), 1500);
      } else {
        setError("Update failed. Please try again.");
      }
    } catch {
      setError("Unable to connect to server. Please try again.");
    } finally {
      setSubmitting(false);
    }
  }

  function renderFieldInput(field) {
    const fieldError = fieldErrors[field.key];
    const errorStyle = fieldError ? styles.inputError : {};
    if (field.type === "custom") {
      return (
        <>
          {field.component}
          {fieldError && <span style={styles.fieldError}>{fieldError}</span>}
        </>
      );
    }
    if (field.type === "select") {
      return (
        <>
          <select
            style={{ ...styles.select, ...errorStyle }}
            value={extraData[field.key] ?? ""}
            onChange={e => handleExtraChange(field.key, e.target.value)}
          >
            <option value="">Select {field.label}</option>
            {field.options?.map(opt => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
          {fieldError && <span style={styles.fieldError}>{fieldError}</span>}
        </>
      );
    }
    if (field.type === "multiselect") {
      return (
        <>
          <div style={{ ...styles.multiWrap, ...errorStyle }}>
            {field.options?.map(opt => {
              const isSelected = (extraData[field.key] || []).includes(opt.value);
              return (
                <button
                  key={opt.value}
                  type="button"
                  onClick={() => handleMultiToggle(field.key, opt.value)}
                  style={{ ...styles.chip, ...(isSelected ? styles.chipSelected : {}) }}
                >
                  {isSelected ? "✓ " : ""}{opt.label}
                </button>
              );
            })}
          </div>
          {fieldError && <span style={styles.fieldError}>{fieldError}</span>}
        </>
      );
    }
    return (
      <>
        <input
          style={{ ...styles.input, ...errorStyle }}
          type={field.type || "text"}
          placeholder={`Enter ${field.label}`}
          value={extraData[field.key] ?? ""}
          onChange={e => handleExtraChange(field.key, e.target.value)}
        />
        {fieldError && <span style={styles.fieldError}>{fieldError}</span>}
      </>
    );
  }

  return (
    <div style={styles.page}>
      <div style={styles.inner}>
        <div style={styles.topRow}>
          <button style={styles.backBtn} onClick={() => router.back()}>← Back</button>
          <h2 style={styles.pageTitle}>Edit {title}</h2>
        </div>
        <div style={styles.cardWrap}>
          <div style={styles.card}>
            <p style={styles.cardTitle}>Update {title}</p>
            <p style={styles.cardSubtitle}>Update the details below</p>
            {error   && <div style={styles.errorBox}>{error}</div>}
            {success && <div style={styles.successBox}>{success}</div>}
            {loading ? <p style={styles.loadingText}>Loading {title} data…</p> : (
              <form onSubmit={handleSubmit} style={styles.form}>
                <div style={styles.field}>
                  <label htmlFor="editform-identifier" style={styles.label}>Identifier</label>
                  <input id="editform-identifier" style={styles.inputDisabled} type="text" value={identifierDisplay} disabled />
                </div>
                {extraFields.map(field => (
                  <div key={field.key} style={styles.field}>
                    {field.type !== "custom" && <label style={styles.label}>{field.label}</label>}
                    {renderFieldInput(field)}
                  </div>
                ))}
                <div style={styles.buttonRow}>
                  <button type="button" style={styles.cancelBtn} onClick={() => router.back()}>Cancel</button>
                  <button
                    type="submit"
                    style={{ ...styles.submitBtn, ...(submitting ? styles.submitBtnDisabled : {}) }}
                    disabled={submitting}
                  >
                    {submitting ? "Saving…" : `Update ${title}`}
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

EditFormSkeleton.propTypes = {
  title: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  paramName: PropTypes.string,
  identifierField: PropTypes.string,
  getStyle: PropTypes.string,
  extraFields: PropTypes.array,
  extraData: PropTypes.object,
  setters: PropTypes.object,
  overrideIdentifier: PropTypes.string,
};