"use client";

import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { useRouter, useParams } from "next/navigation";
import api from "@/api/axios";
import { labelStyle, inputStyle, inputErrorStyle, errText, AlertBox } from "@/components/sharedStyles";

const C = {
  navy: "#363955", mid: "#54668E", light: "#879EC6",
  gray: "#E8E8E8", offWhite: "#F5F6E6", text: "#1e2235",
  muted: "#6b7280", white: "#ffffff",
  error: "#c0392b", errorBg: "#fdf2f2",
};

function useFieldChange(setData, setFieldErrors) {
  function handleChange(key, value) {
    setData(prev => ({ ...prev, [key]: value }));
    setFieldErrors(prev => ({ ...prev, [key]: "" }));
  }
  return handleChange;
}

export function PageShell({ isSidebarOpen, children }) {
  return (
    <div style={{
      position: "fixed", top: "60px", right: 0, bottom: 0,
      left: isSidebarOpen ? "220px" : "55px",
      backgroundColor: "#ffffff",
      fontFamily: "'Segoe UI', sans-serif",
      display: "flex", flexDirection: "column",
      overflow: "hidden", transition: "left 0.2s ease",
    }}>
      {children}
    </div>
  );
} 

export function PageHeader({ onBack, backLabel = "← Back", title, subtitle }) {
  return (
    <div style={{
      background: "#ffffff", padding: "16px 28px",
      display: "flex", alignItems: "center", gap: "14px",
      flexShrink: 0, borderBottom: "1.5px solid #e8eaf0",
    }}>
      <button
        onClick={onBack}
        style={{
          background: "#ffffff", border: `1.5px solid ${C.mid}`,
          color: C.mid, borderRadius: "7px",
          padding: "5px 14px", fontSize: "12px",
          fontWeight: "600", cursor: "pointer",
        }}
      >
        {backLabel}
      </button>
      <div style={{ width: "1px", height: "22px", background: "#e8eaf0" }} />
      <h2 style={{ margin: 0, fontSize: "18px", fontWeight: "700", color: C.navy, letterSpacing: "0.1px" }}>
        {title}
      </h2>
      {subtitle && (
        <>
          <div style={{ width: "1px", height: "22px", background: "#e8eaf0" }} />
          <span style={{ fontSize: "13px", color: C.muted, fontWeight: "500" }}>{subtitle}</span>
        </>
      )}
    </div>
  );
}

export function PageCard({ children }) {
  return (
    <div style={{
      flex: 1, overflow: "auto", padding: "28px 32px",
      display: "flex", alignItems: "flex-start", justifyContent: "center",
    }}>
      <div style={{
        width: "100%", maxWidth: "860px",
        background: C.white, borderRadius: "12px",
        boxShadow: "0 1px 12px rgba(54,57,85,0.07)",
        border: "1.5px solid #e8eaf0", overflow: "hidden",
      }}>
        {children}
      </div>
    </div>
  );
}

function FieldError({ error }) {
  if (!error) return null;
  return <span style={errText}>{error}</span>;
}

PageShell.propTypes = {
  isSidebarOpen: PropTypes.bool.isRequired,
  children: PropTypes.node.isRequired,
};

PageHeader.propTypes = {
  onBack: PropTypes.func.isRequired,
  backLabel: PropTypes.string,
  title: PropTypes.string.isRequired,
  subtitle: PropTypes.string,
};

PageCard.propTypes = {
  children: PropTypes.node.isRequired,
};

FieldError.propTypes = {
  error: PropTypes.string,
};

export default function EditFormSkeleton({
  title, apiPath,
  paramName = "identifier", identifierField = "identifier", getStyle = "path",
  extraFields = [], extraData: externalExtraData = {}, setters = {},
}) {
  const router = useRouter();
  const params = useParams();
  const identifier = decodeURIComponent(params[paramName] || "");

  const [identifierDisplay, setIdentifierDisplay] = useState("");
  const [recordId, setRecordId] = useState(null);
  const [extraData, setExtraData] = useState({});
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  useEffect(() => {
    const handleToggle = (e) => setIsSidebarOpen(e.detail.isOpen);
    globalThis.addEventListener("sidebar-toggle", handleToggle);
    return () => globalThis.removeEventListener("sidebar-toggle", handleToggle);
  }, []);

  const handleExtraChange = useFieldChange(setExtraData, setFieldErrors);

  function handleMultiToggle(key, value) {
    setExtraData(prev => {
      const current = prev[key] || [];
      const updated = current.includes(value) ? current.filter(v => v !== value) : [...current, value];
      return { ...prev, [key]: updated };
    });
    setFieldErrors(prev => ({ ...prev, [key]: "" }));
  }

  useEffect(() => {
    if (!identifier) return;
    async function loadData() {
      try {
        const res = await api.get(`/${apiPath}/get`, { params: { [identifierField]: identifier } });
        const data = res.data;
        setIdentifierDisplay(data?.[identifierField]);
        setRecordId(data.id);
        const prefilled = {};
        extraFields.forEach(field => {
          if (field.type !== "custom" && data[field.key] !== undefined) prefilled[field.key] = data[field.key];
        });
        setExtraData(prefilled);
        Object.entries(setters).forEach(([key, setter]) => { if (data[key] !== undefined) setter(data[key]); });
      } catch (err) {
        if (process.env.NODE_ENV !== "production") console.error(err);
        setError("Could not load data. Please go back and try again.");
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, [identifier, apiPath]);

  function validate() {
    const errors = {};
    extraFields.forEach(field => {
      if (field.type === "custom") {
        const val = externalExtraData[field.key];
        if (val === undefined || val === null || val === "" || (Array.isArray(val) && val.length === 0)) {
          errors[field.key] = `${field.label || field.key} is required.`;
        }
        return;
      }
      if (field.type === "multiselect") {
        if ((extraData[field.key] || []).length === 0) errors[field.key] = `${field.label} is required.`;
      } else if (!String(extraData[field.key] || "").trim()) {
        errors[field.key] = `${field.label} is required.`;
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
    } catch (err) {
      if (process.env.NODE_ENV !== "production") console.error(err);
      setError("Unable to connect to server. Please try again.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <PageShell isSidebarOpen={isSidebarOpen}>
      <PageHeader
        onBack={() => router.back()}
        title={`Edit ${title}`}
        subtitle={identifierDisplay && !loading ? identifierDisplay : null}
      />
      <PageCard>
        <div style={{
          padding: "18px 28px 16px", borderBottom: "1.5px solid #e8eaf0",
          display: "flex", alignItems: "center", justifyContent: "space-between",
        }}>
          <div>
            <p style={{ margin: 0, fontSize: "15px", fontWeight: "700", color: C.navy }}>Update {title}</p>
            <p style={{ margin: "3px 0 0", fontSize: "12px", color: C.muted }}>
              Modify the details below and save your changes
            </p>
          </div>
          <div style={{
            background: "#FFF4E6", borderRadius: "8px",
            padding: "5px 14px", fontSize: "12px",
            fontWeight: "600", color: "#b45309", border: "1px solid #fed7aa",
          }}>
            Editing Record
          </div>
        </div>

        <AlertBox error={error} success={success} />

        {loading ? (
          <div style={{ textAlign: "center", padding: "52px", color: C.muted, fontSize: "14px" }}>
            Loading {title} data…
          </div>
        ) : (
          <form onSubmit={handleSubmit} style={{ padding: "20px 28px 24px" }}>
            <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: "16px 20px" }}>
              <div style={{ display: "flex", flexDirection: "column", gap: "5px" }}>
                <label htmlFor="identifier" style={labelStyle}>Identifier</label>
                <input
                  style={{ ...inputStyle, background: C.offWhite, color: "#9ca3af", cursor: "not-allowed" }}
                  id="identifier" type="text" value={identifierDisplay} disabled
                />
              </div>

              {extraFields.map(field => {
                let fieldElement = null;
                if (field.type === "custom") {
                  fieldElement = (<>{field.component}<FieldError error={fieldErrors[field.key]} /></>);
                } else if (field.type === "select") {
                  fieldElement = (
                    <>
                      <select
                        id={field.key}
                        style={{ ...inputStyle, ...(fieldErrors[field.key] ? inputErrorStyle : {}) }}
                        value={extraData[field.key] ?? ""}
                        onChange={e => handleExtraChange(field.key, e.target.value)}
                      >
                        <option value="">Select {field.label}</option>
                        {field.options?.map(opt => (
                          <option key={opt.value} value={opt.value}>{opt.label}</option>
                        ))}
                      </select>
                      <FieldError error={fieldErrors[field.key]} />
                    </>
                  );
                } else if (field.type === "multiselect") {
                  fieldElement = (
                    <>
                      <div style={{
                        display: "flex", flexWrap: "wrap", gap: "6px", padding: "8px",
                        border: `1.5px solid ${fieldErrors[field.key] ? C.error : C.gray}`,
                        borderRadius: "7px", background: "#fafafa", minHeight: "42px",
                      }}>
                        {field.options?.map(opt => {
                          const isSelected = (extraData[field.key] || []).includes(opt.value);
                          return (
                            <button key={opt.value} type="button"
                              onClick={() => handleMultiToggle(field.key, opt.value)}
                              style={{
                                padding: "3px 11px", borderRadius: "20px", fontSize: "12px",
                                fontWeight: isSelected ? "600" : "500", cursor: "pointer",
                                border: `1.5px solid ${isSelected ? C.mid : C.gray}`,
                                background: isSelected ? `linear-gradient(135deg, ${C.navy}, ${C.mid})` : C.white,
                                color: isSelected ? "#fff" : "#374151",
                              }}
                            >
                              {isSelected ? "✓ " : ""}{opt.label}
                            </button>
                          );
                        })}
                      </div>
                      <FieldError error={fieldErrors[field.key]} />
                    </>
                  );
                } else {
                  fieldElement = (
                    <>
                      <input
                        id={field.key}
                        style={{ ...inputStyle, ...(fieldErrors[field.key] ? inputErrorStyle : {}) }}
                        type={field.type || "text"}
                        placeholder={`Enter ${field.label}`}
                        value={extraData[field.key] ?? ""}
                        onChange={e => handleExtraChange(field.key, e.target.value)}
                      />
                      <FieldError error={fieldErrors[field.key]} />
                    </>
                  );
                }
                return (
                  <div key={field.key} style={{ display: "flex", flexDirection: "column", gap: "5px" }}>
                    {field.type !== "custom" && (
                      <label htmlFor={field.key} style={labelStyle}>{field.label}</label>
                    )}
                    {fieldElement}
                  </div>
                );
              })}
            </div>

            <div style={{
              display: "flex", justifyContent: "flex-end",
              gap: "10px", marginTop: "24px",
              paddingTop: "18px", borderTop: "1.5px solid #e8eaf0",
            }}>
              <button
                type="button" onClick={() => router.back()}
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
                type="submit" disabled={submitting}
                style={{
                  padding: "9px 28px", borderRadius: "7px", border: "none",
                  background: submitting ? "#c4c8d4" : `linear-gradient(135deg, ${C.navy}, ${C.mid})`,
                  color: "#fff", fontSize: "13px",
                  fontWeight: "600", cursor: submitting ? "not-allowed" : "pointer",
                  boxShadow: submitting ? "none" : "0 3px 10px rgba(54,57,85,0.25)",
                }}
              >
                {submitting ? "Saving…" : `Update ${title}`}
              </button>
            </div>
          </form>
        )}
      </PageCard>
    </PageShell>
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
};