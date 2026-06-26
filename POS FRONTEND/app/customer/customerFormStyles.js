export const inputStyle = {
  border: "1px solid #cbd5e1",
  borderRadius: "8px",
  padding: "8px 12px",
  fontSize: "13px",
  outline: "none",
  width: "100%",
  boxSizing: "border-box",
};

export const labelStyle = {
  fontSize: "12px",
  fontWeight: 600,
  color: "#475569",
  marginBottom: "4px",
  display: "block",
};

export const sectionStyle = {
  background: "#f8fafc",
  border: "1px solid #e2e8f0",
  borderRadius: "10px",
  padding: "16px",
  display: "flex",
  flexDirection: "column",
  gap: "12px",
};

export const readOnlyInputStyle = {
  ...inputStyle,
  background: "#f1f5f9",
  color: "#64748b",
  cursor: "not-allowed",
};

export const backButtonStyle = {
  background: "none",
  border: "1px solid #e2e8f0",
  borderRadius: "8px",
  padding: "6px 12px",
  fontSize: "13px",
  cursor: "pointer",
  color: "#64748b",
};

export const errorStyle = {
  background: "#fef2f2",
  border: "1px solid #fecaca",
  color: "#dc2626",
  borderRadius: "8px",
  padding: "10px 14px",
  fontSize: "13px",
  marginBottom: "16px",
};

export const cancelButtonStyle = {
  padding: "10px 20px",
  borderRadius: "8px",
  border: "1px solid #e2e8f0",
  background: "#fff",
  fontSize: "13px",
  fontWeight: 600,
  cursor: "pointer",
  color: "#64748b",
};

export const submitButtonStyle = (loading) => ({
  padding: "10px 24px",
  borderRadius: "8px",
  border: "none",
  background: "#2563eb",
  color: "#fff",
  fontSize: "13px",
  fontWeight: 700,
  cursor: loading ? "not-allowed" : "pointer",
  opacity: loading ? 0.7 : 1,
});
