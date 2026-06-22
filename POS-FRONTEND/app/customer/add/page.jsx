"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import api from "@/api/axios";

const C = {
    navy: "#363955",
    mid: "#54668E",
    text: "#1e2235",
    muted: "#6b7280",
    border: "#e8eaf0",
    red: "#dc2626",
    errorBg: "#fef2f2",
    green: "#166534",
    greenBg: "#f0fdf4",
};

const inputSt = {
    width: "100%",
    height: "36px",
    padding: "0 10px",
    border: "1.5px solid #dcdfe6",
    borderRadius: "7px",
    fontSize: "13px",
    outline: "none",
    boxSizing: "border-box",
    background: "#fff",
    color: C.text,
};

const inputErrSt = { ...inputSt, borderColor: C.red, background: C.errorBg };

const labelSt = {
    display: "block",
    fontSize: "11px",
    fontWeight: "700",
    color: "#4b5563",
    letterSpacing: "0.4px",
    textTransform: "uppercase",
    marginBottom: "4px",
};

const errTextSt = { display: "block", fontSize: "11px", color: C.red, marginTop: "3px" };

const sectionSt = {
    border: `1.5px solid ${C.border}`,
    borderRadius: "10px",
    padding: "14px 16px",
    marginBottom: "14px",
};

const sectionTitleSt = {
    fontSize: "13px",
    fontWeight: "700",
    color: C.navy,
    marginBottom: "10px",
};

const PARTY_TYPE_OPTIONS = ["Customer", "Dealer", "Retailer", "Distributor"];
const CREDIT_TYPE_OPTIONS = [
    { value: "NA", label: "NA" },
    { value: "ADVANCE", label: "Advance" },
    { value: "DUE", label: "Due" },
];

const ADDRESS_FIELDS = [
    { key: "addressLine", label: "Address Line" },
    { key: "city", label: "City" },
    { key: "state", label: "State" },
    { key: "zipCode", label: "Zip Code" },
    { key: "country", label: "Country" },
];

const EMPTY_ADDRESS = { addressLine: "", city: "", state: "", zipCode: "", country: "" };

export default function AddCustomer() {
    const router = useRouter();

    const [identifier, setIdentifier] = useState("");
    const [customerName, setCustomerName] = useState("");
    const [email, setEmail] = useState("");
    const [partyType, setPartyType] = useState("");
    const [credit, setCredit] = useState("");
    const [creditType, setCreditType] = useState("");
    const [creditLimit, setCreditLimit] = useState("");
    const [billingAddress, setBillingAddress] = useState({ ...EMPTY_ADDRESS });
    const [shippingAddress, setShippingAddress] = useState({ ...EMPTY_ADDRESS });

    const [errors, setErrors] = useState({});
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [loading, setLoading] = useState(false);

    function handlePhoneInput(e) {
        setIdentifier(e.target.value.replace(/[^0-9+-]/g, ""));
    }

    function setBillingField(key, val) {
        setBillingAddress((p) => ({ ...p, [key]: val }));
    }

    function setShippingField(key, val) {
        setShippingAddress((p) => ({ ...p, [key]: val }));
    }

    function validate() {
        const e = {};
        if (!identifier.trim()) e.identifier = "Phone number is required.";
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
            const res = await api.post("/customer/add", {
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
                setError(data.message || "Customer already exists.");
                return;
            }
            setSuccess("Customer added successfully");
            setTimeout(() => router.back(), 1200);
        } catch (err) {
            console.error(err);
            setError("Unable to connect to server. Please try again.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div style={{ minHeight: "100vh", fontFamily: "'Segoe UI', sans-serif", background: "#f4f5f9" }}>
            <div
                style={{
                    maxWidth: "880px",
                    background: "#fff",
                    margin: "0 auto",
                    padding: "72px 20px 20px", // ← top padding increased to clear fixed navbar
                    minHeight: "100vh",
                    boxSizing: "border-box",
                }}
            >
                {/* Header — matches other modules' back button row */}
                <div style={{ display: "flex", alignItems: "center", gap: "14px", marginBottom: "16px" }}>
                    <button
                        type="button"
                        onClick={() => router.back()}
                        style={{
                            background: "#fff",
                            border: `1.5px solid ${C.mid}`,
                            color: C.mid,
                            borderRadius: "7px",
                            padding: "5px 14px",
                            fontSize: "12px",
                            fontWeight: "600",
                            cursor: "pointer",
                            whiteSpace: "nowrap",
                        }}
                    >
                        ← Back
                    </button>
                    <div style={{ width: "1px", height: "20px", background: C.border }} />
                    <h2 style={{ margin: 0, fontSize: "17px", fontWeight: "700", color: C.navy }}>Add Customer</h2>
                </div>

                {(error || success) && (
                    <div
                        style={{
                            marginBottom: "14px",
                            padding: "8px 12px",
                            borderRadius: "7px",
                            fontSize: "12px",
                            background: error ? C.errorBg : C.greenBg,
                            border: `1px solid ${error ? "#fca5a5" : "#86efac"}`,
                            color: error ? C.red : C.green,
                        }}
                    >
                        {error || success}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    {/* Customer Details */}
                    <div style={sectionSt}>
                        <div style={sectionTitleSt}>Customer Details</div>
                        <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: "10px 14px" }}>
                            <div>
                                <label style={labelSt}>Phone Number</label>
                                <input type="text" maxLength={13} inputMode="tel" value={identifier} onChange={handlePhoneInput} style={errors.identifier ? inputErrSt : inputSt} />
                                {errors.identifier && <span style={errTextSt}>{errors.identifier}</span>}
                            </div>
                            <div>
                                <label style={labelSt}>Customer Name</label>
                                <input type="text" value={customerName} onChange={(e) => setCustomerName(e.target.value)} style={errors.customerName ? inputErrSt : inputSt} />
                                {errors.customerName && <span style={errTextSt}>{errors.customerName}</span>}
                            </div>
                            <div>
                                <label style={labelSt}>Email</label>
                                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} style={errors.email ? inputErrSt : inputSt} />
                                {errors.email && <span style={errTextSt}>{errors.email}</span>}
                            </div>
                            <div>
                                <label style={labelSt}>Party Type</label>
                                <select value={partyType} onChange={(e) => setPartyType(e.target.value)} style={errors.partyType ? inputErrSt : inputSt}>
                                    <option value="">Select Party Type</option>
                                    {PARTY_TYPE_OPTIONS.map((opt) => <option key={opt} value={opt}>{opt}</option>)}
                                </select>
                                {errors.partyType && <span style={errTextSt}>{errors.partyType}</span>}
                            </div>
                            <div>
                                <label style={labelSt}>Credit</label>
                                <input type="number" min="0" value={credit} onChange={(e) => setCredit(e.target.value)} style={errors.credit ? inputErrSt : inputSt} />
                                {errors.credit && <span style={errTextSt}>{errors.credit}</span>}
                            </div>
                            <div>
                                <label style={labelSt}>Credit Type</label>
                                <select value={creditType} onChange={(e) => setCreditType(e.target.value)} style={errors.creditType ? inputErrSt : inputSt}>
                                    <option value="">Select Credit Type</option>
                                    {CREDIT_TYPE_OPTIONS.map((opt) => <option key={opt.value} value={opt.value}>{opt.label}</option>)}
                                </select>
                                {errors.creditType && <span style={errTextSt}>{errors.creditType}</span>}
                            </div>
                            <div>
                                <label style={labelSt}>Credit Limit</label>
                                <input type="number" min="0" value={creditLimit} onChange={(e) => setCreditLimit(e.target.value)} style={errors.creditLimit ? inputErrSt : inputSt} />
                                {errors.creditLimit && <span style={errTextSt}>{errors.creditLimit}</span>}
                            </div>
                        </div>
                    </div>

                    {/* Billing + Shipping side by side to compress vertical space */}
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "14px" }}>
                        <div style={sectionSt}>
                            <div style={sectionTitleSt}>Billing Address</div>
                            <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
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
                            <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
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

                    {/* Actions */}
                    <div style={{ display: "flex", justifyContent: "flex-end", gap: "10px", marginTop: "16px" }}>
                        <button
                            type="button"
                            onClick={() => router.back()}
                            style={{
                                padding: "9px 22px",
                                borderRadius: "7px",
                                border: `1.5px solid ${C.border}`,
                                background: "#fff",
                                color: "#374151",
                                fontSize: "13px",
                                fontWeight: "600",
                                cursor: "pointer",
                            }}
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={loading}
                            style={{
                                padding: "9px 26px",
                                borderRadius: "7px",
                                border: "none",
                                background: loading ? "#c4c8d4" : `linear-gradient(135deg, ${C.navy}, ${C.mid})`,
                                color: "#fff",
                                fontSize: "13px",
                                fontWeight: "600",
                                cursor: loading ? "not-allowed" : "pointer",
                            }}
                        >
                            {loading ? "Saving…" : "Add Customer"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}