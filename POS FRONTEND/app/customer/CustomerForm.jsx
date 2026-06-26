"use client";
import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { useRouter, useParams, useSearchParams } from "next/navigation";
import axiosInstance from "../api/axiosInstance";
import { AddressSection } from "./AddressSection";
import {
    inputStyle,
    sectionStyle,
    backButtonStyle,
    errorStyle,
    cancelButtonStyle,
    submitButtonStyle,
    labelStyle,
    readOnlyInputStyle,
} from "./customerFormStyles";
import { digitsOnly } from "../lib/fieldUtils";

export default function CustomerForm({ mode = "add" }) {
    const router = useRouter();
    const params = useParams();
    const searchParams = useSearchParams();

    const identifier = mode === "edit" ? decodeURIComponent(params.id || "") : null;
    const phoneFromQuery = mode === "add" ? searchParams.get("phone") || "" : null;

    const [form, setForm] = useState({
        phoneNum: mode === "add" ? phoneFromQuery : "",
        customerName: "",
        username: "",
        billingAddress: {},
        shippingAddress: {},
    });
    const [sameAsShipping, setSameAsShipping] = useState(false);
    const [pageLoading, setPageLoading] = useState(mode === "edit");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        if (mode !== "edit") return;
        const fetchCustomer = async () => {
            try {
                const res = await axiosInstance.get("/customer/update", { params: { identifier } });
                const data = res.data || {};
                setForm({
                    phoneNum: data.phoneNum || data.identifier || identifier,
                    customerName: data.customerName || "",
                    username: data.username || "",
                    billingAddress: data.billingAddress || {},
                    shippingAddress: data.shippingAddress || {},
                });
            } catch (err) {
                setError(err?.response?.data?.message || "Failed to load customer.");
            } finally {
                setPageLoading(false);
            }
        };
        fetchCustomer();
    }, [identifier, mode]);

   
    useEffect(() => {
        if (mode !== "add" || !phoneFromQuery) return;
        setForm((prev) => ({ ...prev, phoneNum: phoneFromQuery }));
    }, [phoneFromQuery, mode]);

    const handleChange = (key, value) => {
        setForm((prev) => ({ ...prev, [key]: value }));
    };

    const handleAddressChange = (section, field, value) => {
        setForm((prev) => ({
            ...prev,
            [section]: { ...prev[section], [field]: value },
        }));
    };

    const isValidEmail = (value) => {
        if (!value || typeof value !== "string") return false;
        if (value.length > 254) return false;

        const atIndex = value.indexOf("@");
        if (atIndex <= 0 || atIndex === value.length - 1) return false;

        const local = value.slice(0, atIndex);
        const domain = value.slice(atIndex + 1);

        if (local.length > 64 || domain.length > 253) return false;
        if (domain.includes("..")) return false;
        if (!domain.includes('.')) return false;

        const labels = domain.split('.');
        for (const label of labels) {
            if (!label || label.length > 63) return false;
            if (label.startsWith('-') || label.endsWith('-')) return false;
            if (!/^[A-Za-z0-9-]+$/.test(label)) return false;
        }

        return true;
    };

    const validateForm = () => {
        if (mode === "add" && !form.phoneNum.trim()) {
            setError("Phone number is required.");
            return false;
        }
        if (mode === "add") {
            const only = digitsOnly(form.phoneNum || "");
            if (only.length !== 10) {
                setError("Phone number must be exactly 10 digits.");
                return false;
            }
        }
        if (form.username.trim() && !isValidEmail(form.username.trim())) {
            setError("Enter a valid email address.");
            return false;
        }
        return true;
    };

    const buildPayload = () => ({
        ...(mode === "edit" && { identifier }),
        ...form,
        billingAddress: sameAsShipping ? { ...form.shippingAddress } : form.billingAddress,
    });

    const submitCustomer = async (payload) => {
        return mode === "add"
            ? await axiosInstance.post("/customer/add", payload)
            : await axiosInstance.put("/customer/update", payload);
    };

    const handleRedirect = () => {
        if (mode === "add" && phoneFromQuery) {
            router.push(`/cart?phone=${encodeURIComponent(form.phoneNum.trim())}`);
        } else {
            router.push("/customer");
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) return;

        setLoading(true);
        setError("");

        try {
            const payload = buildPayload();
            const response = await submitCustomer(payload);

            if (response.data?.success === false) {
                setError(response.data.message || `Failed to ${mode === "add" ? "add" : "update"} customer.`);
                return;
            }

            handleRedirect();
        } finally {
            setLoading(false);
        }
    };

    const title = mode === "add" ? "Add Customer" : "Edit Customer";
    const buttonLabel = mode === "add" ? "Save Customer" : "Update Customer";
    const isPhoneReadOnly = mode === "edit";

    if (pageLoading) {
        return <div className="w-full py-10 text-center text-slate-600">Loading customer...</div>;
    }

    return (
        <div style={{ maxWidth: "680px", margin: "32px auto", padding: "0 16px" }}>
            <div style={{ marginBottom: "20px", display: "flex", alignItems: "center", gap: "12px" }}>
                <button onClick={() => router.back()} style={backButtonStyle}>
                    ← Back
                </button>
                <h1 style={{ margin: 0, fontSize: "18px", fontWeight: 700, color: "#1e293b" }}>{title}</h1>
            </div>

            {error && <div style={errorStyle}>{error}</div>}

            <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
                <div style={sectionStyle}>
                    <h3 style={{ margin: 0, fontSize: "13px", fontWeight: 700, color: "#1e293b" }}>Customer Info</h3>
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px" }}>
                        <div>
                            <label htmlFor="phoneNum" style={labelStyle}>
                                Phone Number {!isPhoneReadOnly && "*"}
                            </label>
                            {isPhoneReadOnly ? (
                                <input
                                    id="phoneNum"
                                    type="tel"
                                    value={form.phoneNum}
                                    readOnly
                                    style={readOnlyInputStyle}
                                />
                            ) : (
                                <input
                                    id="phoneNum"
                                    type="tel"
                                    inputMode="numeric"
                                    value={form.phoneNum}
                                    onChange={(e) => handleChange("phoneNum", digitsOnly(e.target.value))}
                                    placeholder="e.g. 9876543210"
                                    maxLength={10}
                                    required
                                    style={inputStyle}
                                />
                            )}
                        </div>
                        <div>
                            <label htmlFor="customerName" style={labelStyle}>
                                Customer Name
                            </label>
                            <input
                                id="customerName"
                                type="text"
                                value={form.customerName}
                                onChange={(e) => handleChange("customerName", e.target.value)}
                                placeholder="e.g. John Doe"
                                style={inputStyle}
                            />
                        </div>
                        <div>
                            <label htmlFor="username" style={labelStyle}>
                                Email
                            </label>
                            <input
                                id="username"
                                type="email"
                                value={form.username}
                                onChange={(e) => handleChange("username", e.target.value)}
                                placeholder="e.g. john@example.com"
                                style={inputStyle}
                            />
                        </div>
                    </div>
                </div>

                <AddressSection
                    title="Shipping Address"
                    prefix="shippingAddress"
                    values={form.shippingAddress}
                    onChange={handleAddressChange}
                />

                <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                    <input
                        type="checkbox"
                        id="sameAddress"
                        checked={sameAsShipping}
                        onChange={(e) => setSameAsShipping(e.target.checked)}
                        style={{ width: "16px", height: "16px", cursor: "pointer" }}
                    />
                    <label htmlFor="sameAddress" style={{ fontSize: "13px", color: "#475569", cursor: "pointer" }}>
                        Billing address same as shipping
                    </label>
                </div>

                {!sameAsShipping && (
                    <AddressSection
                        title="Billing Address"
                        prefix="billingAddress"
                        values={form.billingAddress}
                        onChange={handleAddressChange}
                    />
                )}

                <div style={{ display: "flex", gap: "10px", justifyContent: "flex-end" }}>
                    <button type="button" onClick={() => router.back()} style={cancelButtonStyle}>
                        Cancel
                    </button>
                    <button type="submit" disabled={loading} style={submitButtonStyle(loading)}>
                        {loading ? "Saving..." : buttonLabel}
                    </button>
                </div>
            </form>
        </div>
    );
}

CustomerForm.propTypes = {
    mode: PropTypes.oneOf(["add", "edit"]).isRequired,
};
