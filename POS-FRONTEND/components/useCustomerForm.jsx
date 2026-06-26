import { useState } from "react";

export const PARTY_TYPES = ["RETAIL", "LOYALTY MEMBER", "WHOLESALER", "DEALER"];
export const BALANCE_TYPES = ["DEBIT", "CREDIT"];

const initialAddress = { addressLine: "", city: "", state: "", country: "India", zipcode: "" };

export function useCustomerForm(initialData = {}) {
    const [form, setForm] = useState({
        identifier: initialData.identifier || "",
        customerName: initialData.customerName || "",
        username: initialData.username || "",
        partyType: initialData.partyType || "RETAIL",
        balance: initialData.balance ?? "",
        balanceType: initialData.balanceType || "DEBIT",
        creditLimit: initialData.creditLimit ?? "",
    });

    const [billing, setBilling] = useState(initialData.billingAddress || { ...initialAddress });
    const [shipping, setShipping] = useState(initialData.shippingAddress || { ...initialAddress });

    const [showBilling, setShowBilling] = useState(!!initialData.billingAddress);
    const [showShipping, setShowShipping] = useState(!!initialData.shippingAddress);
    const [sameAsBilling, setSameAsBilling] = useState(false);

    const handleFormChange = (key, value) => setForm((p) => ({ ...p, [key]: value }));

    const handleBillingChange = (key, val) => {
        setBilling((p) => ({ ...p, [key]: val }));
        if (sameAsBilling) setShipping((p) => ({ ...p, [key]: val }));
    };

    const handleShippingChange = (key, val) => setShipping((p) => ({ ...p, [key]: val }));

    const handleSameAsBilling = (checked) => {
        setSameAsBilling(checked);
        if (checked) setShipping({ ...billing });
    };

    const getPayload = () => {
        return {
            ...form,
            balance: form.balance === "" ? 0 : Number(form.balance),
            creditLimit: form.creditLimit === "" ? 0 : Number(form.creditLimit),
            ...(showBilling && { billingAddress: billing }),
            ...(showShipping && { shippingAddress: sameAsBilling ? billing : shipping }),
        };
    };

    return {
        form, setForm,
        billing, setBilling,
        shipping, setShipping,
        showBilling, setShowBilling,
        showShipping, setShowShipping,
        sameAsBilling,
        handleFormChange,
        handleBillingChange,
        handleShippingChange,
        handleSameAsBilling,
        getPayload,
    };
}