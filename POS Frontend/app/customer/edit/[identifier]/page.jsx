"use client";
 
import { useState, useEffect, useRef } from "react";
import { useRouter, useParams } from "next/navigation";
import api from "@/api/axios";
 
const C = {
    primary: "#000000",
    text: "#1a1a1a",
    muted: "#666666",
    border: "#d1d5db",
    red: "#dc2626",
    errorBg: "#fee2e2",
    green: "#166534",
    greenBg: "#f0fdf4",
    offWhite: "#f4f4f4",
    white: "#ffffff",
};
 
const inputSt = {
    width: "100%",
    height: "42px",
    padding: "10px 14px",
    border: `1.2px solid ${C.border}`,
    borderRadius: "8px",
    fontSize: "13px",
    outline: "none",
    boxSizing: "border-box",
    background: "#fff",
    color: C.text,
};
 
const inputErrSt = { ...inputSt, borderColor: C.red, background: C.errorBg };
const inputReadOnlySt = { ...inputSt, background: "#f7f8fc", color: "#94a3b8", cursor: "not-allowed" };
 
const labelSt = {
    display: "block",
    fontSize: "11px",
    fontWeight: "700",
    color: C.muted,
    letterSpacing: "0.6px",
    textTransform: "uppercase",
    marginBottom: "6px",
};
 
const errTextSt = { display: "block", fontSize: "11px", color: C.red, marginTop: "3px" };
 
const sectionSt = {
    border: `1px solid ${C.border}`,
    borderRadius: "12px",
    padding: "16px 20px",
    marginBottom: "20px",
    background: C.white,
    boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
};
 
const sectionTitleSt = {
    fontSize: "13px",
    fontWeight: "700",
    color: C.text,
    marginBottom: "14px",
};
 
const pageSt = {
    position: "fixed",
    top: "60px",
    right: 0,
    bottom: 0,
    backgroundColor: C.offWhite,
    fontFamily: "'Segoe UI', sans-serif",
    display: "flex",
    flexDirection: "column",
    overflow: "hidden",
    transition: "left 0.2s ease",
};
 
const containerSt = {
    flex: 1,
    overflowY: "auto",
    padding: "24px",
};
 
const cardSt = {
    maxWidth: "900px",
    background: C.white,
    margin: "0 auto",
    padding: "28px",
    borderRadius: "12px",
    boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
    border: `1px solid ${C.border}`,
};
 
const headerRowSt = {
    display: "flex",
    alignItems: "flex-start",
    gap: "16px",
    marginBottom: "24px",
};
 
const backBtnSt = {
    padding: "8px 16px",
    backgroundColor: "transparent",
    color: C.primary,
    border: "1px solid transparent",
    borderRadius: "6px",
    fontSize: "13px",
    fontWeight: "600",
    cursor: "pointer",
    transition: "all 0.2s ease",
    height: "42px",
    flexShrink: 0,
};
 
const titleSt = {
    margin: 0,
    fontSize: "18px",
    fontWeight: "700",
    color: C.text,
};
 
const introTextSt = {
    margin: "6px 0 0",
    color: C.muted,
    fontSize: "13px",
    lineHeight: "1.5",
};
 
const alertBoxSt = {
    marginBottom: "20px",
    padding: "14px 16px",
    borderRadius: "8px",
    fontSize: "13px",
    lineHeight: "1.5",
};
 
const buttonRowSt = {
    display: "flex",
    justifyContent: "flex-end",
    gap: "12px",
    marginTop: "24px",
    flexWrap: "wrap",
};
 
const cancelBtnSt = {
    padding: "10px 20px",
    borderRadius: "8px",
    border: `1px solid ${C.border}`,
    background: C.white,
    color: C.text,
    fontSize: "13px",
    fontWeight: "600",
    cursor: "pointer",
};
 
const submitBtnSt = {
    padding: "10px 22px",
    borderRadius: "8px",
    border: "none",
    background: C.primary,
    color: "#fff",
    fontSize: "13px",
    fontWeight: "600",
    cursor: "pointer",
};
 
const submitBtnDisabledSt = {
    ...submitBtnSt,
    background: "#9ca3af",
    cursor: "not-allowed",
};

const metadataOuterSt = {
    marginTop: "28px",
    padding: "20px",
    background: "#ffffff",
    border: `1px solid ${C.border}`,
    borderRadius: "8px",
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: "32px",
    boxSizing: "border-box",
};

const metadataCardSt = {
    display: "flex",
    flexDirection: "column",
    gap: "12px",
};

const metadataCardTitleSt = {
    margin: 0,
    fontSize: "12px",
    fontWeight: "600",
    color: "#6b7280",
    letterSpacing: "0.5px",
    textTransform: "uppercase",
    marginBottom: "4px",
};

const metadataRowSt = {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    fontSize: "13px",
    color: "#1f2937",
    lineHeight: "1.6",
    gap: "16px",
};

const metadataLabelSt = {
    color: "#6b7280",
    fontWeight: "500",
    fontSize: "13px",
};

const metadataValueSt = {
    fontWeight: "500",
    color: "#1a1a1a",
    textAlign: "right",
    fontSize: "13px",
};
 
function useSidebarOpen() {
    const [isOpen, setIsOpen] = useState(true);
    useEffect(() => {
        const handleToggle = (event) => setIsOpen(event.detail?.isOpen ?? true);
        globalThis.addEventListener("sidebar-toggle", handleToggle);
        return () => globalThis.removeEventListener("sidebar-toggle", handleToggle);
    }, []);
    return isOpen;
}
 
function formatDateTime(value) {
    if (!value) return "—";
    const d = new Date(value);
    if (isNaN(d.getTime())) return value;
    return d.toLocaleString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });
}
 
function AuditCard({ heading, rows }) {
    return (
        <div style={metadataCardSt}>
            <p style={metadataCardTitleSt}>{heading}</p>
            {rows.map(({ label, value }) => (
                <div key={label} style={metadataRowSt}>
                    <span style={metadataLabelSt}>{label}</span>
                    <span style={metadataValueSt}>{value || "—"}</span>
                </div>
            ))}
        </div>
    );
}
 
const PARTY_TYPE_OPTIONS = ["Customer", "Dealer", "Retailer", "Distributor"];
const CREDIT_TYPE_OPTIONS = [
    { value: "NA", label: "NA" },
    { value: "ADVANCE", label: "Advance" },
    { value: "DUE", label: "Due" },
];

// Static option arrays shaped for SingleDropdown (no apiUrl needed — passed as null, overridden via staticOptions)
const PARTY_TYPE_DROPDOWN_OPTIONS = PARTY_TYPE_OPTIONS.map(v => ({ value: v, label: v }));
const CREDIT_TYPE_DROPDOWN_OPTIONS = CREDIT_TYPE_OPTIONS;
 
const ADDRESS_FIELDS = [
    { key: "addressLine", label: "Address Line" },
    { key: "city", label: "City" },
    { key: "state", label: "State" },
    { key: "zipCode", label: "Zip Code" },
    { key: "country", label: "Country" },
];
 
const EMPTY_ADDRESS = { addressLine: "", city: "", state: "", zipCode: "", country: "" };

// Inline static dropdown — same style as SingleDropdown but takes options directly
function StaticSingleDropdown({ label, options, selectedValue, onChange, error }) {
    const [isOpen, setIsOpen] = useState(false);
    const [menuPos, setMenuPos] = useState({ top: 0, left: 0, width: 0 });
    const boxRef = useRef(null);
    const wrapRef = useRef(null);

    useEffect(() => {
        const handleOutside = (e) => {
            if (wrapRef.current && !wrapRef.current.contains(e.target)) setIsOpen(false);
        };
        document.addEventListener("mousedown", handleOutside);
        return () => document.removeEventListener("mousedown", handleOutside);
    }, []);

    const handleToggle = () => {
        if (!isOpen && boxRef.current) {
            const r = boxRef.current.getBoundingClientRect();
            setMenuPos({ top: r.bottom + window.scrollY, left: r.left + window.scrollX, width: r.width });
        }
        setIsOpen(prev => !prev);
    };

    const handleSelect = (value) => {
        onChange(value === selectedValue ? "" : value);
        setIsOpen(false);
    };

    return (
        <>
            <style>{`
                .ssd-wrap { width: 100%; position: relative; font-family: "Segoe UI", sans-serif; }
                .ssd-label { font-size: 11px; font-weight: 700; color: #666666; margin-bottom: 0; margin-top: 0; display: block; letter-spacing: 0.6px; text-transform: uppercase; }
                .ssd-box {
                    width: 100%; padding: 10px 14px; height: 42px;
                    background: #fff; border: 1.2px solid #d1d5db;
                    border-radius: 8px; cursor: pointer;
                    box-sizing: border-box; font-size: 13px;
                    color: #1a1a1a; transition: border-color 0.15s;
                    display: flex; align-items: center; justify-content: space-between;
                }
                .ssd-box.err { border-color: #dc2626; background: #fee2e2; }
                .ssd-box:hover { border-color: #000000; }
                .ssd-box.open { border-color: #1a1a1a; }
                .ssd-chevron { font-size: 10px; color: #999999; transition: transform 0.2s; }
                .ssd-chevron.open { transform: rotate(180deg); }
                .ssd-menu {
                    position: fixed;
                    background: #ffffff; border: 1.5px solid #e8e8e8;
                    border-radius: 8px; margin-top: 4px;
                    max-height: 200px; overflow-y: auto;
                    box-shadow: 0 8px 24px rgba(0,0,0,0.08);
                    z-index: 99999;
                }
                .ssd-item {
                    width: 100%; padding: 9px 12px; display: flex;
                    align-items: center; gap: 9px;
                    cursor: pointer; border: none; border-bottom: 1px solid #f3f4f6;
                    background: transparent;
                    transition: background 0.1s; font-size: 13px; color: #374151;
                    text-align: left;
                }
                .ssd-item:last-child { border-bottom: none; }
                .ssd-item:hover { background: #f5f5f5; }
                .ssd-item.selected { background: rgba(0,0,0,0.06); color: #000000; font-weight: 500; }
            `}</style>
            <div className="ssd-wrap" ref={wrapRef}>
                <label className="ssd-label">{label}</label>
                <button
                    ref={boxRef}
                    type="button"
                    className={`ssd-box${isOpen ? " open" : ""}${error ? " err" : ""}`}
                    onClick={handleToggle}
                    aria-expanded={isOpen}
                    aria-haspopup="listbox"
                >
                    <span>
                        {selectedValue
                            ? options.find(o => o.value === selectedValue)?.label ?? selectedValue
                            : <span style={{ color: "#9ca3af" }}>Select…</span>}
                    </span>
                    <span className={`ssd-chevron${isOpen ? " open" : ""}`}>▼</span>
                </button>
                {isOpen && (
                    <div
                        className="ssd-menu"
                        style={{ top: menuPos.top, left: menuPos.left, width: menuPos.width }}
                        aria-label={`${label} options`}
                    >
                        {options.map(opt => (
                            <button
                                key={opt.value}
                                type="button"
                                className={`ssd-item${selectedValue === opt.value ? " selected" : ""}`}
                                onClick={() => handleSelect(opt.value)}
                            >
                                <span style={{ width: 18, display: "inline-block", textAlign: "center" }}>
                                    {selectedValue === opt.value ? "✓" : ""}
                                </span>
                                {opt.label}
                            </button>
                        ))}
                    </div>
                )}
            </div>
        </>
    );
}
 
export default function EditCustomer() {
    const router = useRouter();
    const params = useParams();
    const identifier = params?.identifier;
    const isSidebarOpen = useSidebarOpen();
 
    const [customerName, setCustomerName] = useState("");
    const [email, setEmail] = useState("");
    const [partyType, setPartyType] = useState("");
    const [credit, setCredit] = useState("");
    const [creditType, setCreditType] = useState("");
    const [creditLimit, setCreditLimit] = useState("");
    const [billingAddress, setBillingAddress] = useState({ ...EMPTY_ADDRESS });
    const [shippingAddress, setShippingAddress] = useState({ ...EMPTY_ADDRESS });
 
    const [auditInfo, setAuditInfo] = useState({
        createdBy: "",
        createdAt: "",
        modifiedBy: "",
        modifiedAt: "",
    });
 
    const [errors, setErrors] = useState({});
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [loading, setLoading] = useState(false);
    const [fetching, setFetching] = useState(true);
 
    useEffect(() => {
        if (!identifier) return;
        api
            .get("/customer/get", { params: { identifier } })
            .then((res) => {
                const d = res.data;
                setCustomerName(d.customerName ?? "");
                setEmail(d.email ?? "");
                setPartyType(d.partyType ?? "");
                setCredit(d.credit ?? "");
                setCreditType(d.creditType ?? "");
                setCreditLimit(d.creditLimit ?? "");
                setBillingAddress({ ...EMPTY_ADDRESS, ...(d.billingAddress || {}) });
                setShippingAddress({ ...EMPTY_ADDRESS, ...(d.shippingAddress || {}) });
                setAuditInfo({
                    createdBy: d.createdBy,
                    createdAt: d.createdAt,
                    modifiedBy: d.modifiedBy,
                    modifiedAt: d.modifiedAt,
                });
            })
            .catch((err) => {
                console.error(err);
                setError("Failed to load customer data.");
            })
            .finally(() => setFetching(false));
    }, [identifier]);
 
    function setBillingField(key, val) {
        setBillingAddress((p) => ({ ...p, [key]: val }));
    }
 
    function setShippingField(key, val) {
        setShippingAddress((p) => ({ ...p, [key]: val }));
    }
 
    function validate() {
        const e = {};
        if (!customerName.trim()) e.customerName = "Customer name is required.";
        if (!email.trim()) {
            e.email = "Email is required.";
        } else if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email.trim())) {
            e.email = "Enter a valid email address.";
        }
        if (!partyType) e.partyType = "Party type is required.";
        if (credit === "" || Number(credit) < 0) e.credit = "Credit is required.";
        if (!creditType) e.creditType = "Credit type is required.";
        if (creditLimit === "" || Number(creditLimit) < 0) e.creditLimit = "Credit limit is required.";
 
        ADDRESS_FIELDS.forEach((f) => {
            if (!billingAddress[f.key]?.trim()) e[`billing_${f.key}`] = `${f.label} is required.`;
            if (!shippingAddress[f.key]?.trim()) e[`shipping_${f.key}`] = `${f.label} is required.`;
        });
 
        setErrors(e);
        return Object.keys(e).length === 0;
    }
 
    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setSuccess("");
        if (!validate()) return;
 
        setLoading(true);
        try {
            const res = await api.post("/customer/update", {
                identifier,
                customerName,
                email,
                partyType,
                credit: Number(credit),
                creditType,
                creditLimit: Number(creditLimit),
                billingAddress,
                shippingAddress,
            });
            const data = res.data;
            if (data?.success === false) {
                setError(data.message || "Failed to update customer.");
                return;
            }
            setSuccess("Customer updated successfully");
            setTimeout(() => router.back(), 1200);
        } catch (err) {
            console.error(err);
            setError("Unable to connect to server. Please try again.");
        } finally {
            setLoading(false);
        }
    }
 
    if (fetching) {
        return (
            <div style={{ ...pageSt, left: isSidebarOpen ? "220px" : "55px", display: "flex", alignItems: "center", justifyContent: "center" }}>
                <div style={{ color: C.muted, fontSize: "14px" }}>Loading customer details…</div>
            </div>
        );
    }
 
    return (
        <div style={{ ...pageSt, left: isSidebarOpen ? "220px" : "55px" }}>
            <div style={containerSt}>
                <div style={cardSt}>
                    <div style={headerRowSt}>
                        <button type="button" onClick={() => router.back()} style={backBtnSt}>⮜ Back</button>
                        <div>
                            <h2 style={titleSt}>Edit Customer</h2>
                            <p style={introTextSt}>Update customer profile details, credit settings, and address information without changing core data.</p>
                        </div>
                    </div>
 
                    {(error || success) && (
                        <div
                            style={{
                                ...alertBoxSt,
                                background: error ? C.errorBg : C.greenBg,
                                border: error ? `1px solid ${C.red}` : `1px solid ${C.green}`,
                                color: error ? C.red : C.green,
                            }}
                        >
                            {error || success}
                        </div>
                    )}
 
                    <form onSubmit={handleSubmit}>
                        <div style={sectionSt}>
                            <div style={sectionTitleSt}>Customer Details</div>
                            <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: "16px" }}>
                                <div>
                                    <label style={labelSt} htmlFor="edit-customer-identifier">Phone Number</label>
                                    <input id="edit-customer-identifier" type="text" value={identifier} readOnly style={inputReadOnlySt} />
                                </div>
                                <div>
                                    <label style={labelSt} htmlFor="edit-customer-name">Customer Name</label>
                                    <input id="edit-customer-name" type="text" value={customerName} onChange={(e) => setCustomerName(e.target.value)} style={errors.customerName ? inputErrSt : inputSt} />
                                    {errors.customerName && <span style={errTextSt}>{errors.customerName}</span>}
                                </div>
                                <div>
                                    <label style={labelSt} htmlFor="edit-customer-email">Email</label>
                                    <input id="edit-customer-email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} style={errors.email ? inputErrSt : inputSt} />
                                    {errors.email && <span style={errTextSt}>{errors.email}</span>}
                                </div>
                                <div>
                                    <StaticSingleDropdown
                                        label="Party Type"
                                        options={PARTY_TYPE_DROPDOWN_OPTIONS}
                                        selectedValue={partyType}
                                        onChange={setPartyType}
                                        error={errors.partyType}
                                    />
                                    {errors.partyType && <span style={errTextSt}>{errors.partyType}</span>}
                                </div>
                                <div>
                                    <label style={labelSt} htmlFor="edit-customer-credit">Credit</label>
                                    <input id="edit-customer-credit" type="number" min="0" value={credit} onChange={(e) => setCredit(e.target.value)} style={errors.credit ? inputErrSt : inputSt} />
                                    {errors.credit && <span style={errTextSt}>{errors.credit}</span>}
                                </div>
                                <div>
                                    <StaticSingleDropdown
                                        label="Credit Type"
                                        options={CREDIT_TYPE_DROPDOWN_OPTIONS}
                                        selectedValue={creditType}
                                        onChange={setCreditType}
                                        error={errors.creditType}
                                    />
                                    {errors.creditType && <span style={errTextSt}>{errors.creditType}</span>}
                                </div>
                                <div>
                                    <label style={labelSt} htmlFor="edit-customer-creditLimit">Credit Limit</label>
                                    <input id="edit-customer-creditLimit" type="number" min="0" value={creditLimit} onChange={(e) => setCreditLimit(e.target.value)} style={errors.creditLimit ? inputErrSt : inputSt} />
                                    {errors.creditLimit && <span style={errTextSt}>{errors.creditLimit}</span>}
                                </div>
                            </div>
                        </div>
 
                        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" }}>
                            <div style={sectionSt}>
                                <div style={sectionTitleSt}>Billing Address</div>
                                <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
                                    {ADDRESS_FIELDS.map((f) => (
                                        <div key={f.key}>
                                            <label style={labelSt}>{f.label}</label>
                                            <input type="text" value={billingAddress[f.key]} onChange={(e) => setBillingField(f.key, e.target.value)} style={errors[`billing_${f.key}`] ? inputErrSt : inputSt} />
                                            {errors[`billing_${f.key}`] && <span style={errTextSt}>{errors[`billing_${f.key}`]}</span>}
                                        </div>
                                    ))}
                                </div>
                            </div>
 
                            <div style={sectionSt}>
                                <div style={sectionTitleSt}>Shipping Address</div>
                                <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
                                    {ADDRESS_FIELDS.map((f) => (
                                        <div key={f.key}>
                                            <label style={labelSt}>{f.label}</label>
                                            <input type="text" value={shippingAddress[f.key]} onChange={(e) => setShippingField(f.key, e.target.value)} style={errors[`shipping_${f.key}`] ? inputErrSt : inputSt} />
                                            {errors[`shipping_${f.key}`] && <span style={errTextSt}>{errors[`shipping_${f.key}`]}</span>}
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>
 
                        <div style={buttonRowSt}>
                            <button type="button" onClick={() => router.back()} style={cancelBtnSt}>Cancel</button>
                            <button type="submit" disabled={loading} style={loading ? submitBtnDisabledSt : submitBtnSt}>
                                {loading ? "Updating…" : "Update Customer"}
                            </button>
                        </div>
                    </form>
 
                    <div style={metadataOuterSt}>
                        <AuditCard
                            heading="Created"
                            rows={[
                                { label: "By", value: auditInfo.createdBy },
                                { label: "At", value: formatDateTime(auditInfo.createdAt) },
                            ]}
                        />
                        <AuditCard
                            heading="Last Modified"
                            rows={[
                                { label: "By", value: auditInfo.modifiedBy },
                                { label: "At", value: formatDateTime(auditInfo.modifiedAt) },
                            ]}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
}