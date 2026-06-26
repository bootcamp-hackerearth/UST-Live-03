import { useState } from "react";
import PropTypes from "prop-types";
import api from "@/api/axios";

export const C = {
    navy: "#363955",
    mid: "#54668E",
    text: "#1e2235",
    muted: "#6b7280",
    border: "#e8eaf0",
    red: "#dc2626",
    errorBg: "#fef2f2",
    green: "#166534",
    greenBg: "#f0fdf4",
    offWhite: "#F5F6E6",
    white: "#ffffff",
};

export const inputSt = {
    width: "100%",
    height: "36px",
    padding: "0 10px",
    borderWidth: "1.5px",
    borderStyle: "solid",
    borderColor: "#dcdfe6",
    borderRadius: "7px",
    fontSize: "13px",
    outline: "none",
    boxSizing: "border-box",
    background: C.white,
    color: C.text,
};
export const inputErrSt = { ...inputSt, borderColor: C.red, background: C.errorBg };
export const inputReadOnlySt = { ...inputSt, background: "#f7f8fc", color: "#94a3b8" };
export const labelSt = {
    display: "block",
    fontSize: "11px",
    fontWeight: "700",
    color: "#4b5563",
    letterSpacing: "0.4px",
    textTransform: "uppercase",
    marginBottom: "4px",
};
export const errTextSt = { display: "block", fontSize: "11px", color: C.red, marginTop: "3px" };
export const sectionSt = {
    border: `1.5px solid ${C.border}`,
    borderRadius: "10px",
    padding: "14px 16px",
    marginBottom: "14px",
};
export const sectionTitleSt = {
    fontSize: "13px",
    fontWeight: "700",
    color: C.navy,
    marginBottom: "10px",
};
export const pageWrapSt = { minHeight: "100vh", fontFamily: "'Segoe UI', sans-serif", background: "#f4f5f9" };
export const pageInnerSt = {
    maxWidth: "880px",
    background: C.white,
    margin: "0 auto",
    padding: "72px 20px 20px",
    minHeight: "100vh",
    boxSizing: "border-box",
};
export const backButtonSt = {
    background: C.white,
    border: `1.5px solid ${C.mid}`,
    color: C.mid,
    borderRadius: "7px",
    padding: "5px 14px",
    fontSize: "12px",
    fontWeight: "600",
    cursor: "pointer",
    whiteSpace: "nowrap",
};
export const cancelButtonSt = {
    padding: "9px 22px",
    borderRadius: "7px",
    border: `1.5px solid ${C.border}`,
    background: C.white,
    color: "#374151",
    fontSize: "13px",
    fontWeight: "600",
    cursor: "pointer",
};
export const detailsGridSt = { display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: "10px 14px" };
export const addressGridSt = { display: "grid", gridTemplateColumns: "1fr 1fr", gap: "14px" };

export const auditFooterWrapSt = {
    marginTop: "14px", padding: "14px",
    background: C.offWhite,
    border: `1.5px solid ${C.border}`,
    borderRadius: "10px",
};

export function submitButtonSt(loading) {
    return {
        padding: "9px 26px",
        borderRadius: "7px",
        border: "none",
        background: loading ? "#c4c8d4" : `linear-gradient(135deg, ${C.navy}, ${C.mid})`,
        color: C.white,
        fontSize: "13px",
        fontWeight: "600",
        cursor: loading ? "not-allowed" : "pointer",
    };
}
export function alertBannerSt(error) {
    return {
        marginBottom: "14px",
        padding: "8px 12px",
        borderRadius: "7px",
        fontSize: "12px",
        background: error ? C.errorBg : C.greenBg,
        border: `1px solid ${error ? "#fca5a5" : "#86efac"}`,
        color: error ? C.red : C.green,
    };
}

export const PARTY_TYPE_OPTIONS = ["Customer", "Dealer", "Retailer", "Distributor"];
export const CREDIT_TYPE_OPTIONS = [
    { value: "NA", label: "NA" },
    { value: "ADVANCE", label: "Advance" },
    { value: "DUE", label: "Due" },
];
export const ADDRESS_FIELDS = [
    { key: "addressLine", label: "Address Line" },
    { key: "city", label: "City" },
    { key: "state", label: "State" },
    { key: "zipCode", label: "Zip Code" },
    { key: "country", label: "Country" },
];
export const EMPTY_ADDRESS = { addressLine: "", city: "", state: "", zipCode: "", country: "" };

export const CUSTOMER_FIELDS = [
    { key: "customerName", label: "Customer Name", type: "text" },
    { key: "email", label: "Email", type: "email" },
    { key: "partyType", label: "Party Type", type: "select", options: PARTY_TYPE_OPTIONS.map((v) => ({ value: v, label: v })), placeholder: "Select Party Type" },
    { key: "credit", label: "Credit", type: "number" },
    { key: "creditType", label: "Credit Type", type: "select", options: CREDIT_TYPE_OPTIONS, placeholder: "Select Credit Type" },
    { key: "creditLimit", label: "Credit Limit", type: "number" },
];

export function formatDateTime(value) {
    if (!value) return "—";
    const d = new Date(value);
    if (Number.isNaN(d.getTime())) return value;
    return d.toLocaleString("en-IN", {
        day: "2-digit", month: "short", year: "numeric",
        hour: "2-digit", minute: "2-digit",
    });
}

const EMAIL_LOCAL_MAX = 64;
const EMAIL_DOMAIN_MAX = 255;
const EMAIL_TLD_MAX = 24;
const EMAIL_PATTERN = new RegExp(
    String.raw`^[^@\s]{1,${EMAIL_LOCAL_MAX}}@[^@\s]{1,${EMAIL_DOMAIN_MAX}}\.[^@\s]{1,${EMAIL_TLD_MAX}}$`
);

export function isValidEmail(value) {
    if (!value || value.length > EMAIL_LOCAL_MAX + EMAIL_DOMAIN_MAX + EMAIL_TLD_MAX + 2) return false;
    return EMAIL_PATTERN.test(value);
}

const PHONE_PATTERN = /^[6-9]\d{9}$/;

export function isValidPhone(value) {
    if (!value) return false;
    return PHONE_PATTERN.test(value.trim());
}

export function buildErrors({
    identifier,
    customerName,
    email,
    partyType,
    credit,
    creditType,
    creditLimit,
    billingAddress,
    shippingAddress,
}) {
    const e = {};
    if (identifier !== undefined) {
        if (!identifier.trim()) {
            e.identifier = "Phone number is required.";
        } else if (!isValidPhone(identifier.trim())) {
            e.identifier = "Enter a valid 10-digit phone number.";
        }
    }
    if (!customerName.trim()) e.customerName = "Customer name is required.";
    if (!email.trim()) {
        e.email = "Email is required.";
    } else if (!isValidEmail(email.trim())) {
        e.email = "Enter a valid email address.";
    }
    if (!partyType) e.partyType = "Party type is required.";
    if (credit === "" || Number(credit) < 0) e.credit = "Credit is required.";
    if (!creditType) e.creditType = "Credit type is required.";
    if (creditLimit === "" || Number(creditLimit) < 0) e.creditLimit = "Credit limit is required.";
    ADDRESS_FIELDS.forEach(({ key, label }) => {
        if (!billingAddress[key]?.trim()) e[`billing_${key}`] = `${label} is required.`;
        if (!shippingAddress[key]?.trim()) e[`shipping_${key}`] = `${label} is required.`;
    });
    return e;
}

export function useCustomerMutation({ endpoint, method, defaultErrorMsg, successMsg, router }) {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    async function submitMutation(payload) {
        setError("");
        setSuccess("");
        setLoading(true);
        try {
            const res = await api[method](endpoint, payload);
            const data = res.data;
            if (data?.success === false) {
                setError(data.message || defaultErrorMsg);
                setLoading(false);
                return false;
            }
            setSuccess(successMsg);
            setTimeout(() => router.back(), 1200);
            return true;
        } catch (err) {
            if (process.env.NODE_ENV !== "production") console.error(err);
            setError("Unable to connect to server. Please try again.");
            setLoading(false);
            return false;
        }
    }

    return { loading, error, setError, success, setSuccess, submitMutation };
}

export function FormField({ id, label, error, children }) {
    return (
        <div>
            <label htmlFor={id} style={labelSt}>{label}</label>
            {children}
            {error && <span style={errTextSt}>{error}</span>}
        </div>
    );
}
FormField.propTypes = {
    id: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    error: PropTypes.string,
    children: PropTypes.node.isRequired,
};

function CustomerFieldInput({ field, value, error, onChange }) {
    const style = error ? inputErrSt : inputSt;
    if (field.type === "select") {
        return (
            <select id={field.key} value={value} onChange={(e) => onChange(field.key, e.target.value)} style={style}>
                <option value="">{field.placeholder}</option>
                {field.options.map(({ value: v, label: l }) => (
                    <option key={v} value={v}>{l}</option>
                ))}
            </select>
        );
    }
    return (
        <input
            id={field.key}
            type={field.type}
            min={field.type === "number" ? "0" : undefined}
            value={value}
            onChange={(e) => onChange(field.key, e.target.value)}
            style={style}
        />
    );
}
CustomerFieldInput.propTypes = {
    field: PropTypes.shape({
        key: PropTypes.string.isRequired,
        type: PropTypes.string.isRequired,
        placeholder: PropTypes.string,
        options: PropTypes.array,
    }).isRequired,
    value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    error: PropTypes.string,
    onChange: PropTypes.func.isRequired,
};

export function CustomerDetailsSection({ values, errors, onChange, extra }) {
    return (
        <div style={sectionSt}>
            <div style={sectionTitleSt}>Customer Details</div>
            <div style={detailsGridSt}>
                {extra}
                {CUSTOMER_FIELDS.map((field) => (
                    <FormField key={field.key} id={field.key} label={field.label} error={errors[field.key]}>
                        <CustomerFieldInput
                            field={field}
                            value={values[field.key]}
                            error={errors[field.key]}
                            onChange={onChange}
                        />
                    </FormField>
                ))}
            </div>
        </div>
    );
}
CustomerDetailsSection.propTypes = {
    values: PropTypes.object.isRequired,
    errors: PropTypes.objectOf(PropTypes.string).isRequired,
    onChange: PropTypes.func.isRequired,
    extra: PropTypes.node,
};

export function AddressSection({ title, prefix, address, onChange, errors }) {
    return (
        <div style={sectionSt}>
            <div style={sectionTitleSt}>{title}</div>
            <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
                {ADDRESS_FIELDS.map(({ key, label }) => {
                    const fieldId = `${prefix}_${key}`;
                    const errorMsg = errors[fieldId];
                    return (
                        <FormField key={key} id={fieldId} label={label} error={errorMsg}>
                            <input
                                id={fieldId}
                                type="text"
                                value={address[key] ?? ""}
                                onChange={(e) => onChange(key, e.target.value)}
                                style={errorMsg ? inputErrSt : inputSt}
                            />
                        </FormField>
                    );
                })}
            </div>
        </div>
    );
}
AddressSection.propTypes = {
    title: PropTypes.string.isRequired,
    prefix: PropTypes.string.isRequired,
    address: PropTypes.objectOf(PropTypes.string).isRequired,
    onChange: PropTypes.func.isRequired,
    errors: PropTypes.objectOf(PropTypes.string).isRequired,
};

export function AddressGrid({ billingAddress, shippingAddress, onBillingChange, onShippingChange, errors }) {
    return (
        <div style={addressGridSt}>
            <AddressSection
                title="Billing Address"
                prefix="billing"
                address={billingAddress}
                onChange={onBillingChange}
                errors={errors}
            />
            <AddressSection
                title="Shipping Address"
                prefix="shipping"
                address={shippingAddress}
                onChange={onShippingChange}
                errors={errors}
            />
        </div>
    );
}
AddressGrid.propTypes = {
    billingAddress: PropTypes.objectOf(PropTypes.string).isRequired,
    shippingAddress: PropTypes.objectOf(PropTypes.string).isRequired,
    onBillingChange: PropTypes.func.isRequired,
    onShippingChange: PropTypes.func.isRequired,
    errors: PropTypes.objectOf(PropTypes.string).isRequired,
};

export function AuditRow({ label, value }) {
    return (
        <div style={{ display: "flex", justifyContent: "space-between", gap: "12px" }}>
            <span style={{ fontSize: "12px", color: C.muted }}>{label}</span>
            <span style={{ fontSize: "13px", fontWeight: "600", color: C.text }}>{value || "—"}</span>
        </div>
    );
}
AuditRow.propTypes = {
    label: PropTypes.string.isRequired,
    value: PropTypes.string,
};

export function AuditCard({ heading, accent, rows }) {
    return (
        <div style={{
            flex: "1 1 200px",
            background: C.white,
            border: `1.5px solid ${C.border}`,
            borderRadius: "8px",
            padding: "12px 16px",
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
                    <AuditRow key={label} label={label} value={value} />
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

export function AuditFooter({ createdBy, createdAt, modifiedBy, modifiedAt, wrapperStyle }) {
    return (
        <div style={wrapperStyle ?? auditFooterWrapSt}>
            <div style={{ display: "flex", gap: "12px", flexWrap: "wrap" }}>
                <AuditCard
                    heading="Created"
                    accent={C.mid}
                    rows={[
                        { label: "By", value: createdBy },
                        { label: "At", value: formatDateTime(createdAt) },
                    ]}
                />
                <AuditCard
                    heading="Last Modified"
                    accent={C.navy}
                    rows={[
                        { label: "By", value: modifiedBy },
                        { label: "At", value: formatDateTime(modifiedAt) },
                    ]}
                />
            </div>
        </div>
    );
}
AuditFooter.propTypes = {
    createdBy: PropTypes.string,
    createdAt: PropTypes.string,
    modifiedBy: PropTypes.string,
    modifiedAt: PropTypes.string,
    wrapperStyle: PropTypes.object,
};

export function PageHeader({ title, onBack }) {
    return (
        <div style={{ display: "flex", alignItems: "center", gap: "14px", marginBottom: "16px" }}>
            <button type="button" onClick={onBack} style={backButtonSt}>
                ← Back
            </button>
            <div style={{ width: "1px", height: "20px", background: C.border }} />
            <h2 style={{ margin: 0, fontSize: "17px", fontWeight: "700", color: C.navy }}>{title}</h2>
        </div>
    );
}
PageHeader.propTypes = {
    title: PropTypes.string.isRequired,
    onBack: PropTypes.func.isRequired,
};

export function AlertBanner({ error, success }) {
    if (!error && !success) return null;
    return (
        <div style={alertBannerSt(error)}>
            {error || success}
        </div>
    );
}
AlertBanner.propTypes = {
    error: PropTypes.string,
    success: PropTypes.string,
};

export function FormActions({ onCancel, loading, submitLabel, savingLabel }) {
    return (
        <div style={{ display: "flex", justifyContent: "flex-end", gap: "10px", marginTop: "16px" }}>
            <button type="button" onClick={onCancel} style={cancelButtonSt}>
                Cancel
            </button>
            <button type="submit" disabled={loading} style={submitButtonSt(loading)}>
                {loading ? savingLabel : submitLabel}
            </button>
        </div>
    );
}
FormActions.propTypes = {
    onCancel: PropTypes.func.isRequired,
    loading: PropTypes.bool.isRequired,
    submitLabel: PropTypes.string.isRequired,
    savingLabel: PropTypes.string.isRequired,
};