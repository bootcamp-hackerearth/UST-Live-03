"use client";
import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import api from "@/api/axios";

const C = {
  navy: "#000000", mid: "#1a1a1a", light: "#333333",
  gray: "#e8e8e8", offWhite: "#f5f5f5", text: "#1a1a1a",
  muted: "#666666", white: "#ffffff",
  error: "#dc2626", errorBg: "#fdf2f2",
};

const styles = {
  page: {
    width: "100%",
    minHeight: "calc(100vh - 60px)",
    backgroundColor: "#ffffff",
    fontFamily: "'Segoe UI', sans-serif",
    display: "flex",
    flexDirection: "column",
    transition: "padding-left 0.2s ease",
    boxSizing: "border-box",
    marginTop: "60px",
  },
  inner: {
    flex: 1, padding: "20px 24px",
    display: "flex", flexDirection: "column", overflow: "hidden",
  },
  topRow: {
    display: "flex", alignItems: "center", gap: "12px",
    marginBottom: "16px", flexShrink: 0, position: "relative",
  },
  backBtn: {
    padding: "7px 16px", backgroundColor: "transparent",
    color: C.navy, border: "none",
    borderRadius: "7px", fontSize: "12px",
    fontWeight: "600", cursor: "pointer", flexShrink: 0,
    transition: "all 0.2s ease", height: "42px",
  },
  pageTitle: {
    position: "absolute", left: "50%", transform: "translateX(-50%)",
    margin: 0, fontSize: "19px", fontWeight: "700",
    color: C.navy, whiteSpace: "nowrap",
  },
  cardWrap: {
    flex: 1, display: "flex",
    alignItems: "center", justifyContent: "center", overflow: "hidden",
  },
  card: {
    background: C.white, borderRadius: "10px",
    boxShadow: "0 2px 12px rgba(0,0,0,0.05)",
    border: `1px solid ${C.gray}`,
    padding: "24px 28px", width: "100%",
    maxWidth: "720px", maxHeight: "100%", overflow: "auto",
  },
  cardTitle: { fontSize: "16px", fontWeight: "700", color: C.navy, margin: "0 0 3px" },
  cardSubtitle: { fontSize: "12px", color: C.muted, marginBottom: "18px" },
  successBox: {
    backgroundColor: "#f4f4f4", border: "1px solid #d1d5db",        
    color: "#1a1a1a", borderRadius: "7px",
    padding: "9px 14px", fontSize: "13px",
    marginBottom: "14px", textAlign: "center", gridColumn: "span 2",
  },
  errorBox: {
    backgroundColor: C.errorBg, border: "1px solid #f5c6c6",        
    color: C.error, borderRadius: "7px",
    padding: "9px 14px", fontSize: "13px",
    marginBottom: "14px", textAlign: "center", gridColumn: "span 2",
  },
  form: {
    display: "grid", gridTemplateColumns: "1fr 1fr",
    columnGap: "20px", rowGap: "12px",
  },
  field: { display: "flex", flexDirection: "column", gap: "4px" },
  dropdownField: { display: "flex", flexDirection: "column", gap: "4px", gridColumn: "span 2" },
  label: { fontSize: "12px", fontWeight: "600", color: "#374151", letterSpacing: "0.2px" },
  inputDisabled: {
    padding: "9px 12px", border: `1.5px solid ${C.gray}`,
    borderRadius: "7px", fontSize: "13px",
    background: C.offWhite, color: "#9ca3af",                  
    boxSizing: "border-box", width: "100%",
    cursor: "not-allowed", outline: "none",
  },
  input: {
    padding: "9px 12px", border: `1.5px solid ${C.gray}`,
    borderRadius: "7px", fontSize: "13px", outline: "none",
    background: "#fafafa", boxSizing: "border-box", width: "100%",
    color: C.text,
  },
  inputError: { borderColor: C.error, background: C.errorBg },    
  fieldError: { fontSize: "11px", color: C.error, marginTop: "2px" },
  buttonRow: {
    display: "flex", gap: "10px", marginTop: "6px", gridColumn: "span 2",
  },
  cancelBtn: {
    flex: 1, padding: "10px",
    background: C.offWhite, color: "#374151",                    
    borderWidth: "1px", borderStyle: "solid", borderColor: C.gray,
    borderRadius: "7px", fontSize: "13px",
    fontWeight: "600", cursor: "pointer",
  },
  submitBtn: {
    flex: 1, padding: "10px",
    background: `linear-gradient(135deg, #000000, #1a1a1a)`,
    color: "#fff", border: "none",
    borderRadius: "7px", fontSize: "13px",
    fontWeight: "600", cursor: "pointer",
    boxShadow: `0 3px 10px rgba(0,0,0,0.2)`,
  },
  submitBtnDisabled: { background: "#c4c8d4", boxShadow: "none", cursor: "not-allowed" },
  loadingText: { textAlign: "center", color: C.muted, fontSize: "14px", padding: "40px 0" },
};

export default function EditPrice() {
  const router = useRouter();
  const params = useParams();
  const rawIdentifier = params.identifier ? decodeURIComponent(params.identifier) : "";
  const [sidebarOpen, setSidebarOpen] = useState(true);

  const [form, setForm] = useState({ mrp: "", sellingPrice: "", costPrice: "", effectiveFrom: "" });
  const [identifier, setIdentifier] = useState("");
  const [fetching, setFetching] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});

  useEffect(() => {
    const handleToggle = (e) => setSidebarOpen(e.detail.isOpen);
    globalThis.addEventListener("sidebar-toggle", handleToggle);
    return () => globalThis.removeEventListener("sidebar-toggle", handleToggle);
  }, []);

  useEffect(() => {
    if (!rawIdentifier) return;
    async function fetchPrice() {
      try {
        const res = await api.get(`/price/get?identifier=${encodeURIComponent(rawIdentifier)}`);
        if (res.data) {
          setIdentifier(res.data.identifier || "");
          setForm({
            mrp: res.data.mrp ?? "",
            sellingPrice: res.data.sellingPrice ?? "",
            costPrice: res.data.costPrice ?? "",
            effectiveFrom: res.data.effectiveFrom ?? "",
          });
        }
      } catch (err) {
        console.error(err);
        setError("Failed to load price specifications.");
      } finally {
        setFetching(false);
      }
    }
    fetchPrice();
  }, [rawIdentifier]);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (fieldErrors[name]) setFieldErrors((prev) => ({ ...prev, [name]: "" }));
    if (error) setError("");
  }

  function validate() {
    const errors = {};
    if (!form.mrp) errors.mrp = "MRP is required.";
    if (!form.sellingPrice) errors.sellingPrice = "Selling price is required.";
    if (!form.costPrice) errors.costPrice = "Cost price is required.";
    if (!form.effectiveFrom) errors.effectiveFrom = "Effective from date is required.";
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError(""); setSuccess("");
    if (!validate()) return;
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
        return;
      }
      setSuccess("Price updated successfully!");
      setTimeout(() => router.push("/price/list"), 1500);
    } catch (err) {
      setError(err.response?.data?.message || "Unable to connect to server.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={{ ...styles.page, paddingLeft: sidebarOpen ? "220px" : "55px" }}>
      <div style={styles.inner}>
        <div style={styles.topRow}>
          <button className="home-back-button" style={styles.backBtn} onClick={() => router.push("/price/list")}>⮜ Back</button>
          <h2 style={styles.pageTitle}>Edit Price</h2>
        </div>
        <div style={styles.cardWrap}>
          <div style={styles.card}>
            <p style={styles.cardTitle}>Update Price Record</p>
            <p style={styles.cardSubtitle}>Modify configuration parameters below</p>
            {error && <div style={styles.errorBox}>{error}</div>}
            {success && <div style={styles.successBox}>{success}</div>}
            
            {fetching ? (
              <p style={styles.loadingText}>Retrieving catalog definitions...</p>
            ) : (
              <form onSubmit={handleSubmit} style={styles.form}>
                <div style={styles.dropdownField}>
                  <label style={styles.label} htmlFor="field-identifier">Product Identifier</label>
                  <input id="field-identifier" style={styles.inputDisabled} type="text" value={identifier} disabled />
                </div>
                <div style={styles.field}>
                  <label style={styles.label} htmlFor="field-mrp">MRP</label>
                  <input name="mrp"
                    id="field-mrp"
                    style={{ ...styles.input, ...(fieldErrors.mrp ? styles.inputError : {}) }}
                    type="number" step="0.01" placeholder="Enter MRP"
                    value={form.mrp} onChange={handleChange} />
                  {fieldErrors.mrp && <span style={styles.fieldError}>{fieldErrors.mrp}</span>}
                </div>
                <div style={styles.field}>
                  <label style={styles.label} htmlFor="field-sellingPrice">Selling Price</label>
                  <input name="sellingPrice"
                    id="field-sellingPrice"
                    style={{ ...styles.input, ...(fieldErrors.sellingPrice ? styles.inputError : {}) }}
                    type="number" step="0.01" placeholder="Enter selling price"
                    value={form.sellingPrice} onChange={handleChange} />
                  {fieldErrors.sellingPrice && <span style={styles.fieldError}>{fieldErrors.sellingPrice}</span>}
                </div>
                <div style={styles.field}>
                  <label style={styles.label} htmlFor="field-costPrice">Cost Price</label>
                  <input name="costPrice"
                    id="field-costPrice"
                    style={{ ...styles.input, ...(fieldErrors.costPrice ? styles.inputError : {}) }}
                    type="number" step="0.01" placeholder="Enter cost price"
                    value={form.costPrice} onChange={handleChange} />
                  {fieldErrors.costPrice && <span style={styles.fieldError}>{fieldErrors.costPrice}</span>}
                </div>
                <div style={styles.field}>
                  <label style={styles.label} htmlFor="field-effectiveFrom">Effective From</label>
                  <input name="effectiveFrom"
                    id="field-effectiveFrom"
                    style={{ ...styles.input, ...(fieldErrors.effectiveFrom ? styles.inputError : {}) }}
                    type="date"
                    value={form.effectiveFrom} onChange={handleChange} />
                  {fieldErrors.effectiveFrom && <span style={styles.fieldError}>{fieldErrors.effectiveFrom}</span>}
                </div>
                <div style={styles.buttonRow}>
                  <button type="button" style={styles.cancelBtn}
                    onClick={() => router.push("/price/list")}>Cancel</button>
                  <button type="submit"
                    style={{ ...styles.submitBtn, ...(loading ? styles.submitBtnDisabled : {}) }}
                    disabled={loading}>
                    {loading ? "Saving…" : "Update Price"}
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