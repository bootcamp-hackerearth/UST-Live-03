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
  noEditNote: { fontSize: "12px", color: "#9ca3af", marginTop: "10px" },
  
  metadataOuterWrapper: {
    marginTop: "28px",
    padding: "20px",
    background: "#ffffff",
    border: "1px solid #e5e7eb",
    borderRadius: "12px",
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: "32px",
    boxSizing: "border-box",
    boxShadow: "0 1px 4px rgba(0,0,0,0.06)",
  },
  
  metadataCard: {
    display: "flex",
    flexDirection: "column",
    gap: "12px",
  },
  
  metadataCardTitle: {
    margin: 0,
    fontSize: "12px",
    fontWeight: "600",
    color: "#6b7280",
    letterSpacing: "0.5px",
    textTransform: "uppercase",
    marginBottom: "4px",
  },
  
  metadataRow: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    fontSize: "13px",
    color: "#1f2937",
    lineHeight: "1.6",
    gap: "16px",
  },
  
  metadataLabel: {
    color: "#6b7280",
    fontWeight: "500",
    fontSize: "13px",
  },
  
  metadataValue: {
    fontWeight: "500",
    color: "#1a1a1a",
    textAlign: "right",
    fontSize: "13px",
  }
};

function clearFieldError(key, setFieldErrors) {
  setFieldErrors(prev => ({ ...prev, [key]: "" }));
}

function formatAuditDate(dateString) {
  if (!dateString) return "—";
  try {
    const date = new Date(dateString);
    if (Number.isNaN(date.getTime())) return "—";
    
    const day = String(date.getDate()).padStart(2, "0");
    const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
    const month = months[date.getMonth()];
    const year = date.getFullYear();
    
    let hours = date.getHours();
    const minutes = String(date.getMinutes()).padStart(2, "0");
    const ampm = hours >= 12 ? "pm" : "am";
    hours = hours % 12;
    hours = hours || 12;
    
    return `${day} ${month} ${year}, ${hours}:${minutes} ${ampm}`;
  } catch {
    return "—";
  }
}

function AuditCard({ heading, rows }) {
  return (
    <div style={styles.metadataCard}>
      <p style={styles.metadataCardTitle}>{heading}</p>
      {rows.map(({ label, value }) => (
        <div key={label} style={styles.metadataRow}>
          <span style={styles.metadataLabel}>{label}</span>
          <span style={styles.metadataValue}>{value || "—"}</span>
        </div>
      ))}
    </div>
  );
}

AuditCard.propTypes = {
  heading: PropTypes.string.isRequired,
  rows: PropTypes.arrayOf(
    PropTypes.shape({
      label: PropTypes.string.isRequired,
      value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    })
  ).isRequired,
};

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

  const [auditInfo, setAuditInfo] = useState({
    createdBy: "",
    createdAt: "",
    modifiedBy: "",
    modifiedAt: ""
  });

  useEffect(() => {
    if (!identifier) return;
    async function loadData() {
      try {
        const res = await api.get(`/${apiPath}/get`, { params: { [identifierField]: identifier } });
        const data = res.data;
        setIdentifierDisplay(data[identifierField]);
        setRecordId(data.id);
        
        setAuditInfo({
          createdBy: data.createdBy || "",
          createdAt: data.createdAt || "",
          modifiedBy: data.modifiedBy || "",
          modifiedAt: data.modifiedAt || ""
        });

        const prefilled = {};
        extraFields.forEach(field => {
          if (field.type !== "custom" && data[field.key] !== undefined) prefilled[field.key] = data[field.key];
        });
        setExtraData(prefilled);
        Object.entries(setters).forEach(([key, setter]) => { if (data[key] !== undefined) setter(data[key]); });
      } catch (err) {
        const status = err.response?.status;
        const errorMsg = err.response?.data?.message || err.message || "Unknown error";
        if (status === 403) {
          setError("You do not have permission to access this record.");
        } else if (status === 404) {
          setError(errorMsg || "Record not found. Please check the identifier and try again.");
        } else {
          setError("Could not load data. Please go back and try again.");
        }
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
      const val = String(extraData[field.key] || "").trim();
      if (!val) {
        errors[field.key] = `${field.label} is required.`;
      } else if (field.validate) {
        const msg = field.validate(val);
        if (msg) errors[field.key] = msg;
      }
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
      const payload = {
        [identifierField]: identifierDisplay,
        ...extraData,
        ...externalExtraData,
      };
      if (recordId != null) payload.id = recordId;
      const res = await api.put(`/${apiPath}/update`, payload);
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
          <button style={styles.backBtn} onClick={() => router.push(`/${apiPath}/list`)}>⮜ Back</button>
          <h2 style={styles.pageTitle}>Edit {title}</h2>
        </div>
        <div style={styles.cardWrap}>
          <div style={styles.card}>
            <p style={styles.cardTitle}>Update {title}</p>
            <p style={styles.cardSubtitle}>Update the details below</p>
            {error   && <div style={styles.errorBox}>{error}</div>}
            {success && <div style={styles.successBox}>{success}</div>}
            {loading ? <p style={styles.loadingText}>Loading {title} data…</p> : (
              <>
                {extraFields.length === 0 ? (
                  <>
                    <div style={styles.field}>
                      <label htmlFor="editform-identifier" style={styles.label}>Identifier</label>
                      <input id="editform-identifier" style={styles.inputDisabled} type="text" value={identifierDisplay} disabled />
                    </div>
                    <p style={styles.noEditNote}>The identifier cannot be edited. This record has no other fields to update.</p>
                  </>
                ) : (
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
                    <button type="button" style={styles.cancelBtn} onClick={() => router.push(`/${apiPath}/list`)}>Cancel</button>
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

                <div style={styles.metadataOuterWrapper}>
                  <AuditCard
                    heading="Created"
                    rows={[
                      { label: "By", value: auditInfo.createdBy },
                      { label: "At", value: formatAuditDate(auditInfo.createdAt) },
                    ]}
                  />
                  <AuditCard
                    heading="Last Modified"
                    rows={[
                      { label: "By", value: auditInfo.modifiedBy },
                      { label: "At", value: formatAuditDate(auditInfo.modifiedAt) },
                    ]}
                  />
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