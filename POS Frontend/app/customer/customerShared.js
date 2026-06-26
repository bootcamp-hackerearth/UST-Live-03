export const C = {
    primary: "#000000",
    text: "#1a1a1a",
    muted: "#666666",
    border: "#d1d5db",
    red: "#dc2626",
    errorBg: "#fee2e2",
    green: "#166534",
    greenBg: "#f0fdf4",
    white: "#ffffff",
    offWhite: "#f4f4f4",
};

export const inputSt = {
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

export const inputErrSt = { ...inputSt, borderColor: C.red, background: C.errorBg };

export const labelSt = {
    display: "block",
    fontSize: "11px",
    fontWeight: "700",
    color: C.muted,
    letterSpacing: "0.6px",
    textTransform: "uppercase",
    marginBottom: "6px",
};

export const errTextSt = { display: "block", fontSize: "11px", color: C.red, marginTop: "3px" };

export const sectionSt = {
    border: `1px solid ${C.border}`,
    borderRadius: "12px",
    padding: "16px 20px",
    marginBottom: "20px",
    background: C.white,
    boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
};

export const sectionTitleSt = {
    fontSize: "13px",
    fontWeight: "700",
    color: C.text,
    marginBottom: "14px",
};

export const pageSt = {
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

export const containerSt = {
    flex: 1,
    overflowY: "auto",
    padding: "24px",
};

export const cardSt = {
    maxWidth: "900px",
    background: C.white,
    margin: "0 auto",
    padding: "28px",
    borderRadius: "12px",
    boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
    border: `1px solid ${C.border}`,
};

export const headerRowSt = {
    display: "flex",
    alignItems: "flex-start",
    gap: "16px",
    marginBottom: "24px",
};

export const backBtnSt = {
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

export const titleSt = {
    margin: 0,
    fontSize: "18px",
    fontWeight: "700",
    color: C.text,
};

export const introTextSt = {
    margin: "6px 0 0",
    color: C.muted,
    fontSize: "13px",
    lineHeight: "1.5",
};

export const alertBoxSt = {
    marginBottom: "20px",
    padding: "14px 16px",
    borderRadius: "8px",
    fontSize: "13px",
    lineHeight: "1.5",
};

export const buttonRowSt = {
    display: "flex",
    justifyContent: "flex-end",
    gap: "12px",
    marginTop: "24px",
    flexWrap: "wrap",
};

export const cancelBtnSt = {
    padding: "10px 20px",
    borderRadius: "8px",
    border: `1px solid ${C.border}`,
    background: C.white,
    color: C.text,
    fontSize: "13px",
    fontWeight: "600",
    cursor: "pointer",
};

export const submitBtnSt = {
    padding: "10px 22px",
    borderRadius: "8px",
    border: "none",
    background: C.primary,
    color: "#fff",
    fontSize: "13px",
    fontWeight: "600",
    cursor: "pointer",
};

export const submitBtnDisabledSt = {
    ...submitBtnSt,
    background: "#9ca3af",
    cursor: "not-allowed",
};

export const PARTY_TYPE_OPTIONS = ["Customer", "Dealer", "Retailer", "Distributor"];
export const CREDIT_TYPE_OPTIONS = [
    { value: "NA", label: "NA" },
    { value: "ADVANCE", label: "Advance" },
    { value: "DUE", label: "Due" },
];

export const PARTY_TYPE_DROPDOWN_OPTIONS = PARTY_TYPE_OPTIONS.map(v => ({ value: v, label: v }));
export const CREDIT_TYPE_DROPDOWN_OPTIONS = CREDIT_TYPE_OPTIONS;

export const ADDRESS_FIELDS = [
    { key: "addressLine", label: "Address Line" },
    { key: "city", label: "City" },
    { key: "state", label: "State" },
    { key: "zipCode", label: "Zip Code" },
    { key: "country", label: "Country" },
];

export const EMPTY_ADDRESS = { addressLine: "", city: "", state: "", zipCode: "", country: "" };

const EMAIL_RE = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;

export function makeFieldSetter(setAddress) {
    return (key, val) => setAddress(p => ({ ...p, [key]: val }));
}

export function validateCustomerCommon({ customerName, email, partyType, credit, creditType, creditLimit, billingAddress, shippingAddress }) {
    const e = {};
    if (!customerName.trim()) e.customerName = "Customer name is required.";
    if (email.trim()) {
        const t = email.trim();
        if (t.length > 320 || EMAIL_RE.exec(t) === null) e.email = "Enter a valid email address.";
    } else {
        e.email = "Email is required.";
    }
    if (!partyType) e.partyType = "Party type is required.";
    if (credit === "" || Number(credit) < 0) e.credit = "Credit is required.";
    if (!creditType) e.creditType = "Credit type is required.";
    if (creditLimit === "" || Number(creditLimit) < 0) e.creditLimit = "Credit limit is required.";
    ADDRESS_FIELDS.forEach((f) => {
        if (!billingAddress[f.key]?.trim()) e[`billing_${f.key}`] = `${f.label} is required.`;
        if (!shippingAddress[f.key]?.trim()) e[`shipping_${f.key}`] = `${f.label} is required.`;
    });
    return e;
}
