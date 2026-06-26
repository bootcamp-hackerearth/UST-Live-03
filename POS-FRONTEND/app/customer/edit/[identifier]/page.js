"use client";

import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import api from "../../../components/Axios";
import {
    fieldWrap,
    labelClass,
    EMPTY_ADDRESS,
    SectionHeading,
    AddressFieldGroup,
    CustomerInfoFields,
    RecordInfoPanel,
    buildAddressErrors,
    getInputClass,
    validateCustomerCommonFields,
    createAddressChangeHandler,
} from "../../../components/CustomerFormShared";

export default function EditCustomer() {
    const router = useRouter();
    const params = useParams();
    const identifier = params.identifier ? decodeURIComponent(params.identifier) : "";

    const [customerName, setCustomerName] = useState("");
    const [email, setEmail] = useState("");
    const [partyType, setPartyType] = useState("");
    const [credit, setCredit] = useState("");
    const [creditLimit, setCreditLimit] = useState("");
    const [creditType, setCreditType] = useState("");
    const [billingAddress, setBillingAddress] = useState({ ...EMPTY_ADDRESS });
    const [shippingAddress, setShippingAddress] = useState({ ...EMPTY_ADDRESS });

    const [auditInfo, setAuditInfo] = useState({ createdBy: null, createdOn: null, modifiedBy: null, modifiedOn: null });
    const [errors, setErrors] = useState({});
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        if (!identifier) return;
        let isMounted = true;
        async function loadData() {
            try {
                const res = await api.get(`/customer/get?identifier=${encodeURIComponent(identifier)}`);
                if (isMounted) {
                    const data = res.data;
                    setCustomerName(data.customerName || "");
                    setEmail(data.email || "");
                    setPartyType(data.partyType || "");
                    setCredit(data.credit ?? "");
                    setCreditLimit(data.creditLimit ?? "");
                    setCreditType(data.creditType || "");
                    setBillingAddress({
                        addressLine: data.billingAddress?.addressLine || "",
                        city: data.billingAddress?.city || "",
                        state: data.billingAddress?.state || "",
                        country: data.billingAddress?.country || "",
                        zipCode: data.billingAddress?.zipCode || "",
                    });
                    setShippingAddress({
                        addressLine: data.shippingAddress?.addressLine || "",
                        city: data.shippingAddress?.city || "",
                        state: data.shippingAddress?.state || "",
                        country: data.shippingAddress?.country || "",
                        zipCode: data.shippingAddress?.zipCode || "",
                    });
                    setAuditInfo({
                        createdBy: data.createdBy || null,
                        createdOn: data.createdOn || null,
                        modifiedBy: data.modifiedBy || null,
                        modifiedOn: data.modifiedOn || null,
                    });
                }
            } catch (err) {
                console.error(err);
            } finally {
                if (isMounted) setLoading(false);
            }
        }
        loadData();
        return () => { isMounted = false; };
    }, [identifier]);

    const handleBillingChange = createAddressChangeHandler("billing", setBillingAddress, errors, setErrors);
    const handleShippingChange = createAddressChangeHandler("shipping", setShippingAddress, errors, setErrors);

    function handleCustomerInfoChange(key, val) {
        if (key === "customerName") setCustomerName(val);
        if (key === "email") setEmail(val);
        if (key === "partyType") setPartyType(val);
        if (key === "creditType") setCreditType(val);
        if (key === "credit") setCredit(val);
        if (key === "creditLimit") setCreditLimit(val);
        if (errors[key]) setErrors((prev) => ({ ...prev, [key]: "" }));
    }

    function validate() {
        const errs = {};
        Object.assign(errs, validateCustomerCommonFields({ customerName, email, partyType, creditType, credit, creditLimit }));
        Object.assign(errs, buildAddressErrors(billingAddress, "billing"));
        Object.assign(errs, buildAddressErrors(shippingAddress, "shipping"));

        setErrors(errs);
        return Object.keys(errs).length === 0;
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setSuccess("");
        if (!validate()) return;
        setSubmitting(true);
        try {
            const res = await api.put("/customer/update", {
                identifier,
                customerName,
                email,
                partyType,
                credit: Number(credit),
                creditLimit: Number(creditLimit),
                creditType,
                billingAddress: { ...billingAddress },
                shippingAddress: { ...shippingAddress },
            });
            const data = res.data;
            if (data?.identifier) {
                setSuccess("Customer updated successfully");
                setTimeout(() => router.back(), 1500);
            } else {
                setError("Update failed. Please try again.");
            }
        } catch (err) {
            setError(err?.response?.data?.message || "Unable to connect to server.");
        } finally {
            setSubmitting(false);
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
                        Edit Customer
                    </h2>
                </div>

                <div className="flex-1 flex items-start justify-center overflow-auto">
                    <div className="bg-white rounded-xl shadow-[0_2px_10px_rgba(0,0,0,0.06)] p-6 md:p-8 w-full max-w-[720px]">
                        <p className="text-base md:text-[17px] font-bold text-[#1a1a1a] m-0 mb-1">Update Customer</p>
                        <p className="text-xs md:text-sm text-gray-400 mb-5">Update the details below</p>

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

                        {loading ? (
                            <p className="text-center text-gray-400 text-sm py-10">Loading customer data…</p>
                        ) : (
                            <>
                                <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-3.5">

                                    <SectionHeading title="Customer Info" />

                                    <div className={fieldWrap}>
                                        <label htmlFor="identifier" className={labelClass}>Identifier</label>
                                        <input
                                            id="identifier"
                                            className="py-2 px-3 border-[1.5px] border-gray-200 rounded-lg text-sm bg-gray-100 text-gray-400 box-border w-full cursor-not-allowed outline-none"
                                            type="text"
                                            value={identifier}
                                            disabled
                                        />
                                    </div>

                                    <CustomerInfoFields
                                        values={{ customerName, email, partyType, creditType, credit, creditLimit }}
                                        errors={errors}
                                        getInputClass={inputClass}
                                        onChange={handleCustomerInfoChange}
                                    />

                                    <AddressFieldGroup
                                        title="Billing Address"
                                        address={billingAddress}
                                        errors={errors}
                                        prefix="billing"
                                        getInputClass={inputClass}
                                        onChange={handleBillingChange}
                                    />

                                    <AddressFieldGroup
                                        title="Shipping Address"
                                        address={shippingAddress}
                                        errors={errors}
                                        prefix="shipping"
                                        getInputClass={inputClass}
                                        onChange={handleShippingChange}
                                    />

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
                                            className={`flex-1 p-2.5 text-white border-none rounded-lg text-sm font-semibold transition-all ${submitting ? "bg-gray-400 cursor-not-allowed" : "bg-brand cursor-pointer hover:bg-brand-hover"}`}
                                            disabled={submitting}
                                        >
                                            {submitting ? "Saving…" : "Update Customer"}
                                        </button>
                                    </div>
                                </form>

                                <RecordInfoPanel auditInfo={auditInfo} />
                            </>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}