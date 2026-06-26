"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import api from "../../components/Axios";
import {
    fieldWrap,
    labelClass,
    EMPTY_ADDRESS,
    FieldError,
    SectionHeading,
    AddressFieldGroup,
    CustomerInfoFields,
    buildAddressErrors,
    getInputClass,
    validateCustomerCommonFields,
    createAddressChangeHandler,
} from "../../components/CustomerFormShared";

export default function AddCustomer() {
    const router = useRouter();

    const [phone, setPhone] = useState("");
    const [customerName, setCustomerName] = useState("");
    const [email, setEmail] = useState("");
    const [partyType, setPartyType] = useState("");
    const [credit, setCredit] = useState("");
    const [creditLimit, setCreditLimit] = useState("");
    const [creditType, setCreditType] = useState("");
    const [billingAddress, setBillingAddress] = useState({ ...EMPTY_ADDRESS });
    const [shippingAddress, setShippingAddress] = useState({ ...EMPTY_ADDRESS });
    const [sameAsBilling, setSameAsBilling] = useState(false);

    const [errors, setErrors] = useState({});
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [loading, setLoading] = useState(false);

    const handleBillingChange = createAddressChangeHandler(
        "billing",
        setBillingAddress,
        errors,
        setErrors,
        (key, value) => {
            if (sameAsBilling) setShippingAddress((prev) => ({ ...prev, [key]: value }));
        }
    );

    const handleShippingChange = createAddressChangeHandler("shipping", setShippingAddress, errors, setErrors);

    function handleSameAsBilling(checked) {
        setSameAsBilling(checked);
        if (checked) setShippingAddress({ ...billingAddress });
    }

    function validate() {
        const errs = {};

        if (!phone.trim()) errs.phone = "Phone number is required.";
        else if (!/^\d{10}$/.test(phone)) errs.phone = "Enter a valid 10-digit phone number.";

        Object.assign(errs, validateCustomerCommonFields({ customerName, email, partyType, creditType, credit, creditLimit }));
        Object.assign(errs, buildAddressErrors(billingAddress, "billing"));
        if (!sameAsBilling) Object.assign(errs, buildAddressErrors(shippingAddress, "shipping"));

        setErrors(errs);
        return Object.keys(errs).length === 0;
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setSuccess("");
        if (!validate()) return;
        setLoading(true);

        const payload = {
            identifier: phone,
            customerName,
            email,
            partyType,
            credit: Number(credit),
            creditLimit: Number(creditLimit),
            creditType,
            billingAddress: { ...billingAddress },
            shippingAddress: sameAsBilling ? { ...billingAddress } : { ...shippingAddress },
        };

        try {
            const res = await api.post("/customer/add", payload);
            const data = res.data;
            if (data.success === false) {
                setError(data.message || "Customer already exists.");
                setLoading(false);
                return;
            }
            if (data.identifier) {
                setSuccess("Customer added successfully");
                setTimeout(() => router.back(), 1500);
            } else {
                setError("Failed to add. Please try again.");
            }
        } catch {
            setError("Unable to connect to server. Please try again.");
        } finally {
            setLoading(false);
        }
    }

    const inputClass = getInputClass(errors);

    return (
        <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-[#f9fafb] font-sans flex flex-col overflow-hidden">
            <div className="flex-1 p-5 md:p-6 flex flex-col overflow-hidden">
                <div className="flex items-center gap-3 mb-4 shrink-0 relative">
                    <button
                        type="button"
                        className="py-2 px-4 bg-transparent text-brand border-[1.5px] border-brand rounded-lg text-xs md:text-sm font-semibold cursor-pointer shrink-0 transition-colors hover:bg-brand/5"
                        onClick={() => router.back()}
                    >
                        &larr; Back
                    </button>
                    <h2 className="absolute left-1/2 -translate-x-1/2 m-0 text-xl font-bold text-brand whitespace-nowrap">
                        Add Customer
                    </h2>
                </div>

                <div className="flex-1 flex items-start justify-center overflow-auto">
                    <div className="bg-white rounded-xl shadow-[0_2px_10px_rgba(0,0,0,0.06)] p-6 md:p-8 w-full max-w-[720px]">
                        <p className="text-base md:text-[17px] font-bold text-[#1a1a1a] m-0 mb-1">New Customer</p>
                        <p className="text-xs md:text-sm text-gray-400 mb-5">Fill in the details below</p>

                        {error && (
                            <div className="bg-[#fff5f5] border border-[#fca5a5] text-[#c53030] rounded-lg p-2.5 md:p-3.5 text-xs md:text-sm mb-4 text-center">
                                {error}
                            </div>
                        )}
                        {success && (
                            <div className="bg-[#f0fff4] border border-[#9ae6b4] text-[#276749] rounded-lg p-2.5 md:p-3.5 text-xs md:text-sm mb-4 text-center">
                                {success}
                            </div>
                        )}

                        <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-3.5">

                            <SectionHeading title="Customer Info" />

                            <div className={fieldWrap}>
                                <label className={labelClass} htmlFor="phone">Phone Number</label>
                                <input
                                    id="phone"
                                    className={inputClass("phone")}
                                    type="text"
                                    inputMode="numeric"
                                    placeholder="Enter phone number"
                                    value={phone}
                                    onChange={(e) => {
                                        const digitsOnly = e.target.value.replaceAll(/\D/g, "").slice(0, 10);
                                        setPhone(digitsOnly);
                                        if (errors.phone) setErrors((prev) => ({ ...prev, phone: "" }));
                                    }}
                                />
                                <FieldError message={errors.phone} />
                            </div>

                            <CustomerInfoFields
                                values={{ customerName, email, partyType, creditType, credit, creditLimit }}
                                errors={errors}
                                getInputClass={inputClass}
                                onChange={(key, val) => {
                                    if (key === "customerName") setCustomerName(val);
                                    if (key === "email") setEmail(val);
                                    if (key === "partyType") setPartyType(val);
                                    if (key === "creditType") setCreditType(val);
                                    if (key === "credit") setCredit(val);
                                    if (key === "creditLimit") setCreditLimit(val);
                                    if (errors[key]) setErrors((prev) => ({ ...prev, [key]: "" }));
                                }}
                            />

                            <AddressFieldGroup
                                title="Billing Address"
                                address={billingAddress}
                                errors={errors}
                                prefix="billing"
                                getInputClass={inputClass}
                                onChange={handleBillingChange}
                            />

                            <div className="col-span-1 md:col-span-2 flex items-center gap-2 mt-1">
                                <input
                                    id="sameAsBilling"
                                    type="checkbox"
                                    className="w-4 h-4 accent-brand cursor-pointer"
                                    checked={sameAsBilling}
                                    onChange={(e) => handleSameAsBilling(e.target.checked)}
                                />
                                <label htmlFor="sameAsBilling" className="text-xs md:text-sm font-semibold text-gray-600 cursor-pointer">
                                    Shipping address same as billing
                                </label>
                            </div>

                            {!sameAsBilling && (
                                <AddressFieldGroup
                                    title="Shipping Address"
                                    address={shippingAddress}
                                    errors={errors}
                                    prefix="shipping"
                                    getInputClass={inputClass}
                                    onChange={handleShippingChange}
                                />
                            )}

                            <div className="flex gap-3 mt-2 col-span-1 md:col-span-2">
                                <button
                                    type="button"
                                    className="flex-1 p-2.5 bg-gray-100 text-gray-600 border-none rounded-lg text-sm font-semibold cursor-pointer transition-colors hover:bg-gray-200"
                                    onClick={() => router.back()}
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className={`flex-1 p-2.5 text-white border-none rounded-lg text-sm font-semibold transition-all ${loading ? "bg-gray-400 cursor-not-allowed" : "bg-brand cursor-pointer hover:bg-brand-hover"}`}
                                    disabled={loading}
                                >
                                    {loading ? "Saving…" : "Add Customer"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}