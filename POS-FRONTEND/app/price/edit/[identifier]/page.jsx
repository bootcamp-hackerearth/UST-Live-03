"use client";
import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { useRouter, useParams } from "next/navigation";
import api from "@/api/axios";

const C = {
  navy: "#363955", mid: "#54668E", light: "#879EC6",
  gray: "#E8E8E8", offWhite: "#F5F6E6", text: "#1e2235",
  muted: "#6b7280", white: "#ffffff",
  error: "#c0392b", errorBg: "#fdf2f2",
};

function formatDateTime(value) {
  if (!value) return "—";
  const d = new Date(value);
  if (isNaN(d.getTime())) return value;
  return d.toLocaleString("en-IN", {
    day: "2-digit", month: "short", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  });
}

async function fetchPriceData(rawIdentifier) {
  const res = await api.get(`/price/get?identifier=${encodeURIComponent(rawIdentifier)}`);
  return res.data;
}

export default function EditPrice() {
  const router = useRouter();
  const params = useParams();
  const rawIdentifier = params.identifier ? decodeURIComponent(params.identifier) : "";

  const [form, setForm] = useState({ mrp: "", sellingPrice: "", costPrice: "", effectiveFrom: "" });
  const [identifier, setIdentifier] = useState("");
  const [auditInfo, setAuditInfo] = useState({
    createdBy: "", createdAt: "", modifiedBy: "", modifiedAt: "",
  });
  const [fetching, setFetching] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  useEffect(() => {
    const handleToggle = (e) => setIsSidebarOpen(e.detail.isOpen);
    globalThis.addEventListener("sidebar-toggle", handleToggle);
    return () => globalThis.removeEventListener("sidebar-toggle", handleToggle);
  }, []);

  useEffect(() => {
    if (!rawIdentifier) return;
    fetchPriceData(rawIdentifier)
      .then((data) => {
        if (data) {
          setIdentifier(data.identifier || "");
          setForm({
            mrp: data.mrp ?? "",
            sellingPrice: data.sellingPrice ?? "",
            costPrice: data.costPrice ?? "",
            effectiveFrom: data.effectiveFrom ?? "",
          });
          setAuditInfo({
            createdBy: data.createdBy,
            createdAt: data.createdAt,
            modifiedBy: data.modifiedBy,
            modifiedAt: data.modifiedAt,
          });
        }
      })
      .catch((err) => {
        if (process.env.NODE_ENV !== "production") console.error(err);
        setError("Failed to load price specifications.");
      })
      .finally(() => setFetching(false));
  }, [rawIdentifier]);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (fieldErrors[name]) setFieldErrors((prev) => ({ ...prev, [name]: "" }));
    if (error) setError("");
  }

  function validate() {
    return validatePriceForm(form, setFieldErrors);
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError(""); setSuccess("");
    if (!validate()) return;
    await submitPriceUpdate({ identifier, form }, setError, setSuccess, setLoading, router);
  }

  return (
    <EditPriceView
      form={form}
      identifier={identifier}
      auditInfo={auditInfo}
      fetching={fetching}
      error={error}
      success={success}
      loading={loading}
      fieldErrors={fieldErrors}
      isSidebarOpen={isSidebarOpen}
      router={router}
      handleChange={handleChange}
      handleSubmit={handleSubmit}
    />
  );
}

function EditPriceView({ form, identifier, auditInfo, fetching, error, success, loading, fieldErrors, isSidebarOpen, router, handleChange, handleSubmit }) {
  const headerSection = renderHeader(router, identifier, fetching);
  const alertsSection = (error || success) ? renderAlerts(error, success) : null;
  const contentSection = fetching
    ? renderLoading()
    : renderForm(identifier, form, fieldErrors, handleChange, handleSubmit, loading, router);
  const auditSection = fetching ? null : renderAuditFooter(auditInfo);

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
        {headerSection}
      </div>

      <div style={{
        flex: 1, overflow: "auto",
        padding: "28px 32px",
        display: "flex", alignItems: "flex-start", justifyContent: "center",
      }}>
        <div style={{
          width: "100%", maxWidth: "860px",
          background: C.white, borderRadius: "12px",
          boxShadow: "0 1px 12px rgba(54,57,85,0.07)",
          border: "1.5px solid #e8eaf0",
          overflow: "hidden",
        }}>
          {alertsSection}
          {contentSection}
          {auditSection}
        </div>
      </div>
    </div>
  );
}

function renderHeader(router, identifier, fetching) {
  return (
    <>
      <button
        onClick={() => router.push("/price/list")}
        style={{
          background: "#ffffff", border: `1.5px solid ${C.mid}`,
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
        Edit Price
      </h2>
      {identifier && !fetching && (
        <>
          <div style={{ width: "1px", height: "22px", background: "#e8eaf0" }} />
          <span style={{ fontSize: "13px", color: C.muted, fontWeight: "500" }}>
            {identifier}
          </span>
        </>
      )}
    </>
  );
}

function renderAlerts(error, success) {
  return (
    <div style={{ padding: "12px 28px 0" }}>
      {error && (
        <div style={{
          background: C.errorBg, border: "1px solid #f5c6c6",
          color: C.error, borderRadius: "7px",
          padding: "9px 14px", fontSize: "13px",
        }}>
          {error}
        </div>
      )}
      {success && (
        <div style={{
          background: "#f0fdf4", border: "1px solid #86efac",
          color: "#166534", borderRadius: "7px",
          padding: "9px 14px", fontSize: "13px",
        }}>
          {success}
        </div>
      )}
    </div>
  );
}

function renderLoading() {
  return (
    <div style={{
      textAlign: "center", padding: "52px",
      color: C.muted, fontSize: "14px",
    }}>
      Loading Price data…
    </div>
  );
}

function renderForm(identifier, form, fieldErrors, handleChange, handleSubmit, loading, router) {
  return (
    <form onSubmit={handleSubmit} style={{ padding: "20px 28px 24px" }}>
      <div style={{
        display: "grid",
        gridTemplateColumns: "repeat(3, 1fr)",
        gap: "16px 20px",
      }}>

        <div style={{ gridColumn: "1 / -1", display: "flex", flexDirection: "column", gap: "5px" }}>
          <label htmlFor="identifier" style={labelStyle}>Product Identifier</label>
          <input
            id="identifier"
            style={{
              ...inputStyle,
              background: C.offWhite, color: "#9ca3af",
              cursor: "not-allowed",
            }}
            type="text"
            value={identifier}
            disabled
          />
        </div>

        <div style={{ display: "flex", flexDirection: "column", gap: "5px" }}>
          <label htmlFor="mrp" style={labelStyle}>MRP</label>
          <input
            id="mrp" name="mrp"
            style={{ ...inputStyle, ...(fieldErrors.mrp ? inputErrorStyle : {}) }}
            type="number" step="0.01" placeholder="Enter MRP"
            value={form.mrp} onChange={handleChange}
          />
          {fieldErrors.mrp && <span style={errText}>{fieldErrors.mrp}</span>}
        </div>

        <div style={{ display: "flex", flexDirection: "column", gap: "5px" }}>
          <label htmlFor="sellingPrice" style={labelStyle}>Selling Price</label>
          <input
            id="sellingPrice" name="sellingPrice"
            style={{ ...inputStyle, ...(fieldErrors.sellingPrice ? inputErrorStyle : {}) }}
            type="number" step="0.01" placeholder="Enter selling price"
            value={form.sellingPrice} onChange={handleChange}
          />
          {fieldErrors.sellingPrice && <span style={errText}>{fieldErrors.sellingPrice}</span>}
        </div>

        <div style={{ display: "flex", flexDirection: "column", gap: "5px" }}>
          <label htmlFor="costPrice" style={labelStyle}>Cost Price</label>
          <input
            id="costPrice" name="costPrice"
            style={{ ...inputStyle, ...(fieldErrors.costPrice ? inputErrorStyle : {}) }}
            type="number" step="0.01" placeholder="Enter cost price"
            value={form.costPrice} onChange={handleChange}
          />
          {fieldErrors.costPrice && <span style={errText}>{fieldErrors.costPrice}</span>}
        </div>

        <div style={{ display: "flex", flexDirection: "column", gap: "5px" }}>
          <label htmlFor="effectiveFrom" style={labelStyle}>Effective From</label>
          <input
            id="effectiveFrom" name="effectiveFrom"
            style={{ ...inputStyle, ...(fieldErrors.effectiveFrom ? inputErrorStyle : {}) }}
            type="date"
            value={form.effectiveFrom} onChange={handleChange}
          />
          {fieldErrors.effectiveFrom && <span style={errText}>{fieldErrors.effectiveFrom}</span>}
        </div>

      </div>

      <div style={{
        display: "flex", justifyContent: "flex-end",
        gap: "10px", marginTop: "24px",
        paddingTop: "18px", borderTop: "1.5px solid #e8eaf0",
      }}>
        <button
          type="button"
          onClick={() => router.push("/price/list")}
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
          {loading ? "Saving…" : "Update Price"}
        </button>
      </div>
    </form>
  );
}

function AuditCard({ heading, accent, rows }) {
  return (
    <div style={{
      flex: "1 1 200px",
      background: C.white, border: "1.5px solid #e8eaf0",
      borderRadius: "8px", padding: "12px 16px",
    }}>
      <div style={{
        fontSize: "11px", fontWeight: "700", color: accent,
        textTransform: "uppercase", letterSpacing: "0.5px",
        marginBottom: "8px",
      }}>
        {heading}
      </div>
      <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
        {rows.map(({ label, value }) => (
          <div key={label} style={{ display: "flex", justifyContent: "space-between", gap: "12px" }}>
            <span style={{ fontSize: "12px", color: C.muted }}>{label}</span>
            <span style={{ fontSize: "13px", fontWeight: "600", color: C.text }}>{value || "—"}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

AuditCard.propTypes = {
  heading: PropTypes.string.isRequired,
  accent: PropTypes.string.isRequired,
  rows: PropTypes.arrayOf(PropTypes.shape({
    label: PropTypes.string.isRequired,
    value: PropTypes.string,
  })).isRequired,
};

function renderAuditFooter(auditInfo) {
  return (
    <div style={{
      margin: "4px 28px 24px", padding: "14px",
      background: C.offWhite, border: "1.5px solid #e8eaf0",
      borderRadius: "10px",
    }}>
      <div style={{ display: "flex", gap: "12px", flexWrap: "wrap" }}>
        <AuditCard
          heading="Created"
          accent={C.mid}
          rows={[
            { label: "By", value: auditInfo.createdBy },
            { label: "At", value: formatDateTime(auditInfo.createdAt) },
          ]}
        />
        <AuditCard
          heading="Last Modified"
          accent={C.navy}
          rows={[
            { label: "By", value: auditInfo.modifiedBy },
            { label: "At", value: formatDateTime(auditInfo.modifiedAt) },
          ]}
        />
      </div>
    </div>
  );
}

EditPriceView.propTypes = {
  form: PropTypes.object.isRequired,
  identifier: PropTypes.string,
  auditInfo: PropTypes.shape({
    createdBy: PropTypes.string,
    createdAt: PropTypes.string,
    modifiedBy: PropTypes.string,
    modifiedAt: PropTypes.string,
  }).isRequired,
  fetching: PropTypes.bool.isRequired,
  error: PropTypes.string,
  success: PropTypes.string,
  loading: PropTypes.bool.isRequired,
  fieldErrors: PropTypes.object.isRequired,
  isSidebarOpen: PropTypes.bool.isRequired,
  router: PropTypes.object.isRequired,
  handleChange: PropTypes.func.isRequired,
  handleSubmit: PropTypes.func.isRequired,
};

const labelStyle = {
  fontSize: "11px", fontWeight: "700",
  color: "#4b5563", letterSpacing: "0.4px",
  textTransform: "uppercase",
};

const inputStyle = {
  padding: "9px 12px", border: "1.5px solid #E8E8E8",
  borderRadius: "7px", fontSize: "13px", outline: "none",
  background: "#fafafa", boxSizing: "border-box", width: "100%",
  color: "#1e2235",
};

const inputErrorStyle = {
  borderColor: "#c0392b", background: "#fdf2f2",
};

const errText = {
  fontSize: "11px", color: "#c0392b", marginTop: "2px",
};

function validatePriceForm(form, setFieldErrors) {
  const errors = {};
  if (!form.mrp) errors.mrp = "MRP is required.";
  if (!form.sellingPrice) errors.sellingPrice = "Selling price is required.";
  if (!form.costPrice) errors.costPrice = "Cost price is required.";
  if (!form.effectiveFrom) errors.effectiveFrom = "Effective from date is required.";
  setFieldErrors(errors);
  return Object.keys(errors).length === 0;
}

async function submitPriceUpdate({ identifier, form }, setError, setSuccess, setLoading, router) {
  setLoading(true);
  try {
    const res = await api.post("/price/update", {
      identifier,
      mrp: form.mrp,
      sellingPrice: form.sellingPrice,
      costPrice: form.costPrice,
      effectiveFrom: form.effectiveFrom,
    });
    const data = res.data;
    if (data.success === false) {
      setError(data.message || "Failed to update price details.");
      return false;
    }
    setSuccess("Price updated successfully!");
    setTimeout(() => router.push("/price/list"), 1500);
    return true;
  } catch (err) {
    setError(err.response?.data?.message || "Unable to connect to server.");
    return false;
  } finally {
    setLoading(false);
  }
}