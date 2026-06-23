"use client";
import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";

const baseInputClass =
    "py-2 px-3 border-[1.5px] rounded-lg text-sm outline-none bg-[#fafaf8] box-border w-full transition-all focus:border-blue-500 border-gray-300";

function FieldError({ message }) {
    if (!message) return null;
    return <span className="text-[11px] text-[#e53e3e] mt-0.5 block">{message}</span>;
}
FieldError.propTypes = { message: PropTypes.string };

function SectionHeading({ title }) {
    return (
        <div className="col-span-1 md:col-span-2 mt-4 mb-1">
            <p className="text-xs font-bold text-gray-500 uppercase tracking-widest border-b border-gray-100 pb-1">
                {title}
            </p>
        </div>
    );
}
SectionHeading.propTypes = { title: PropTypes.string.isRequired };

const PARTY_TYPE_OPTIONS = ["Customer", "Member", "Dealer"];
const CREDIT_TYPE_OPTIONS = ["Prepaid", "Postpaid", "Credit Line"];
const EMPTY_ADDRESS = { addressLine: "", city: "", state: "", country: "", zipCode: "" };

export default function CustomerForm({ initialData, onSubmitAction, submitLabel = "Save Customer", isEdit = false }) {
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

    useEffect(() => {
        if (initialData) {
            setPhone(initialData.phoneNo || "");
            setCustomerName(initialData.customerName || "");
            setEmail(initialData.email || "");
            setPartyType(initialData.partyType || "");
            setCredit(initialData.credit ?? "");
            setCreditLimit(initialData.creditLimit ?? "");
            setCreditType(initialData.creditType || "");
            setBillingAddress(initialData.billingAddress || { ...EMPTY_ADDRESS });
            setShippingAddress(initialData.shippingAddress || { ...EMPTY_ADDRESS });
            const isSame = JSON.stringify(initialData.billingAddress) === JSON.stringify(initialData.shippingAddress);
            setSameAsBilling(isSame);
        }
    }, [initialData]);

    function handleBillingChange(key, value) {
        setBillingAddress((prev) => {
            const updated = { ...prev, [key]: value };
            if (sameAsBilling) setShippingAddress((shipPrev) => ({ ...shipPrev, [key]: value }));
            return updated;
        });
        if (errors[`billing_${key}`]) setErrors((prev) => ({ ...prev, [`billing_${key}`]: "" }));
    }

    function handleShippingChange(key, value) {
        setShippingAddress((prev) => ({ ...prev, [key]: value }));
        if (errors[`shipping_${key}`]) setErrors((prev) => ({ ...prev, [`shipping_${key}`]: "" }));
    }

    function handleSameAsBilling(checked) {
        setSameAsBilling(checked);
        if (checked) setShippingAddress({ ...billingAddress });
    }

    function validate() {
        const errs = {};
        const cleanPhone = phone.replaceAll(/\D/g, "");
        if (!phone.trim()) errs.phone = "Phone number is required.";
        else if (cleanPhone.length !== 10) errs.phone = "Phone number must be exactly 10 digits long.";
        
        if (!customerName.trim()) errs.customerName = "Customer name is required.";
        
        const trimmedEmail = email.trim();
        if (!trimmedEmail) {
            errs.email = "Email is required.";
        } else if (trimmedEmail.length > 254) {
            errs.email = "Email address exceeds maximum allowed length.";
        } else {
            const emailRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/;
            if (!emailRegex.test(trimmedEmail)) errs.email = "Enter a valid email address.";
        }
        
        if (!partyType) errs.partyType = "Party type is required.";
        if (!creditType) errs.creditType = "Credit type is required.";
        if (credit === "") errs.credit = "Credit is required.";
        else if (Number.isNaN(Number(credit)) || Number(credit) < 0) errs.credit = "Enter a valid credit amount.";
        if (creditLimit === "") errs.creditLimit = "Credit limit is required.";
        else if (Number.isNaN(Number(creditLimit)) || Number(creditLimit) < 0) errs.creditLimit = "Enter a valid credit limit.";

        const addressFields = ["addressLine", "city", "state", "country", "zipCode"];
        addressFields.forEach((f) => {
            if (!billingAddress[f]?.trim()) errs[`billing_${f}`] = `${f === "addressLine" ? "Address line" : f.charAt(0).toUpperCase() + f.slice(1)} is required.`;
        });
        if (!sameAsBilling) {
            addressFields.forEach((f) => {
                if (!shippingAddress[f]?.trim()) errs[`shipping_${f}`] = `${f === "addressLine" ? "Address line" : f.charAt(0).toUpperCase() + f.slice(1)} is required.`;
            });
        }
        setErrors(errs);
        return Object.keys(errs).length === 0;
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setSuccess("");
        if (!validate()) return;
        setLoading(true);

        const cleanPhone = phone.replaceAll(/\D/g, "");
        const payload = {
            identifier: cleanPhone,
            phoneNo: cleanPhone,
            customerName,
            email: email.trim(),
            partyType,
            credit: Number(credit),
            creditLimit: Number(creditLimit),
            creditType,
            billingAddress: { ...billingAddress },
            shippingAddress: sameAsBilling ? { ...billingAddress } : { ...shippingAddress },
        };

        try {
            await onSubmitAction(payload, setSuccess, setError);
            setTimeout(() => router.back(), 1500);
        } catch (err) {
            setError(err.message || "An unhandled execution event surfaced.");
        } finally {
            setLoading(false);
        }
    }

    const getInputClass = (errKey) => `${baseInputClass} ${errors[errKey] ? "border-[#e53e3e] focus:border-[#e53e3e]" : "focus:border-blue-500"}`;

    return (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-5 md:p-7 w-full max-w-[680px] my-auto">
            <div className="flex items-center justify-between mb-5 border-b border-gray-100 pb-4">
                <div>
                    <h2 className="text-lg font-bold text-gray-800 m-0">{isEdit ? "Edit Customer" : "Add Customer"}</h2>
                    <p className="text-xs text-gray-400 mt-0.5">{isEdit ? "Modify system settings identity metadata" : "Register a 10-digit profile standard identity"}</p>
                </div>
                <button type="button" className="py-1.5 px-3 bg-white text-gray-600 border border-gray-300 rounded-lg text-xs font-semibold hover:bg-gray-50 transition-colors" onClick={() => router.back()}>
                    &larr; Back
                </button>
            </div>
            {error && <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg p-3 text-sm mb-4 text-center">{error}</div>}
            {success && <div className="bg-green-50 border border-green-200 text-green-700 rounded-lg p-3 text-sm mb-4 text-center">{success}</div>}
            
            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-x-5 gap-y-3.5">
                <SectionHeading title="Customer Base Info" />
                <div className="flex flex-col gap-1">
                    <label htmlFor="phone" className="text-xs font-semibold text-gray-600">Phone Number (10 Digits)</label>
                    <input id="phone" className={getInputClass("phone")} type="text" maxLength={10} placeholder="9876543210" disabled={isEdit} value={phone} onChange={(e) => {
                        setPhone(e.target.value.replaceAll(/\D/g, ""));
                        if (errors.phone) setErrors((prev) => ({ ...prev, phone: "" }));
                    }} />
                    <FieldError message={errors.phone} />
                </div>
                <div className="flex flex-col gap-1">
                    <label htmlFor="customerName" className="text-xs font-semibold text-gray-600">Customer Name</label>
                    <input id="customerName" className={getInputClass("customerName")} type="text" placeholder="John Doe" value={customerName} onChange={(e) => {
                        setCustomerName(e.target.value);
                        if (errors.customerName) setErrors((prev) => ({ ...prev, customerName: "" }));
                    }} />
                    <FieldError message={errors.customerName} />
                </div>
                <div className="flex flex-col gap-1">
                    <label htmlFor="email" className="text-xs font-semibold text-gray-600">Email Address</label>
                    <input id="email" className={getInputClass("email")} type="text" placeholder="john.doe@example.com" value={email} onChange={(e) => {
                        setEmail(e.target.value);
                        if (errors.email) setErrors((prev) => ({ ...prev, email: "" }));
                    }} />
                    <FieldError message={errors.email} />
                </div>
                <div className="flex flex-col gap-1">
                    <label htmlFor="partyType" className="text-xs font-semibold text-gray-600">Party Type</label>
                    <select id="partyType" className={getInputClass("partyType")} value={partyType} onChange={(e) => {
                        setPartyType(e.target.value);
                        if (errors.partyType) setErrors((prev) => ({ ...prev, partyType: "" }));
                    }}>
                        <option value="">Select Party Type</option>
                        {PARTY_TYPE_OPTIONS.map((opt) => <option key={opt} value={opt}>{opt}</option>)}
                    </select>
                    <FieldError message={errors.partyType} />
                </div>

                <SectionHeading title="Credit Metrics" />
                <div className="flex flex-col gap-1">
                    <label htmlFor="creditType" className="text-xs font-semibold text-gray-600">Credit Type</label>
                    <select id="creditType" className={getInputClass("creditType")} value={creditType} onChange={(e) => {
                        setCreditType(e.target.value);
                        if (errors.creditType) setErrors((prev) => ({ ...prev, creditType: "" }));
                    }}>
                        <option value="">Select Credit Type</option>
                        {CREDIT_TYPE_OPTIONS.map((opt) => <option key={opt} value={opt}>{opt}</option>)}
                    </select>
                    <FieldError message={errors.creditType} />
                </div>
                <div className="flex flex-col gap-1">
                    <label htmlFor="creditBalance" className="text-xs font-semibold text-gray-600">Credit Balance</label>
                    <input id="creditBalance" className={getInputClass("credit")} type="number" placeholder="0" value={credit} onChange={(e) => {
                        setCredit(e.target.value);
                        if (errors.credit) setErrors((prev) => ({ ...prev, credit: "" }));
                    }} />
                    <FieldError message={errors.credit} />
                </div>
                <div className="flex flex-col gap-1 md:col-span-2">
                    <label htmlFor="creditLimit" className="text-xs font-semibold text-gray-600">Credit Limit</label>
                    <input id="creditLimit" className={getInputClass("creditLimit")} type="number" placeholder="50000" value={creditLimit} onChange={(e) => {
                        setCreditLimit(e.target.value);
                        if (errors.creditLimit) setErrors((prev) => ({ ...prev, creditLimit: "" }));
                    }} />
                    <FieldError message={errors.creditLimit} />
                </div>

                <SectionHeading title="Billing Address Setup" />
                <div className="flex flex-col gap-1 md:col-span-2">
                    <label htmlFor="billing_addressLine" className="text-xs font-semibold text-gray-600">Address Line</label>
                    <input id="billing_addressLine" className={getInputClass("billing_addressLine")} type="text" placeholder="123 Main St" value={billingAddress.addressLine} onChange={(e) => handleBillingChange("addressLine", e.target.value)} />
                    <FieldError message={errors.billing_addressLine} />
                </div>
                <div className="flex flex-col gap-1">
                    <label htmlFor="billing_city" className="text-xs font-semibold text-gray-600">City</label>
                    <input id="billing_city" className={getInputClass("billing_city")} type="text" placeholder="Los Angeles" value={billingAddress.city} onChange={(e) => handleBillingChange("city", e.target.value)} />
                    <FieldError message={errors.billing_city} />
                </div>
                <div className="flex flex-col gap-1">
                    <label htmlFor="billing_state" className="text-xs font-semibold text-gray-600">State</label>
                    <input id="billing_state" className={getInputClass("billing_state")} type="text" placeholder="California" value={billingAddress.state} onChange={(e) => handleBillingChange("state", e.target.value)} />
                    <FieldError message={errors.billing_state} />
                </div>
                <div className="flex flex-col gap-1">
                    <label htmlFor="billing_country" className="text-xs font-semibold text-gray-600">Country</label>
                    <input id="billing_country" className={getInputClass("billing_country")} type="text" placeholder="United States" value={billingAddress.country} onChange={(e) => handleBillingChange("country", e.target.value)} />
                    <FieldError message={errors.billing_country} />
                </div>
                <div className="flex flex-col gap-1">
                    <label htmlFor="billing_zipCode" className="text-xs font-semibold text-gray-600">Zip Code</label>
                    <input id="billing_zipCode" className={getInputClass("billing_zipCode")} type="text" placeholder="90001" value={billingAddress.zipCode} onChange={(e) => handleBillingChange("zipCode", e.target.value)} />
                    <FieldError message={errors.billing_zipCode} />
                </div>

                <div className="col-span-1 md:col-span-2 flex items-center gap-2 py-2.5 border-t border-b border-gray-100 my-1">
                    <input id="sameAsBilling" type="checkbox" className="w-4 h-4 rounded text-blue-500 cursor-pointer accent-blue-600" checked={sameAsBilling} onChange={(e) => handleSameAsBilling(e.target.checked)} />
                    <label htmlFor="sameAsBilling" className="text-xs font-medium text-gray-600 cursor-pointer select-none">Shipping address details are identical to billing address</label>
                </div>

                {!sameAsBilling && (
                    <>
                        <SectionHeading title="Shipping Address Setup" />
                        <div className="flex flex-col gap-1 md:col-span-2">
                            <label htmlFor="shipping_addressLine" className="text-xs font-semibold text-gray-600">Address Line</label>
                            <input id="shipping_addressLine" className={getInputClass("shipping_addressLine")} type="text" placeholder="456 Delivery Rd" value={shippingAddress.addressLine} onChange={(e) => handleShippingChange("addressLine", e.target.value)} />
                            <FieldError message={errors.shipping_addressLine} />
                        </div>
                        <div className="flex flex-col gap-1">
                            <label htmlFor="shipping_city" className="text-xs font-semibold text-gray-600">City</label>
                            <input id="shipping_city" className={getInputClass("shipping_city")} type="text" placeholder="Los Angeles" value={shippingAddress.city} onChange={(e) => handleShippingChange("city", e.target.value)} />
                            <FieldError message={errors.shipping_city} />
                        </div>
                        <div className="flex flex-col gap-1">
                            <label htmlFor="shipping_state" className="text-xs font-semibold text-gray-600">State</label>
                            <input id="shipping_state" className={getInputClass("shipping_state")} type="text" placeholder="California" value={shippingAddress.state} onChange={(e) => handleShippingChange("state", e.target.value)} />
                            <FieldError message={errors.shipping_state} />
                        </div>
                        <div className="flex flex-col gap-1">
                            <label htmlFor="shipping_country" className="text-xs font-semibold text-gray-600">Country</label>
                            <input id="shipping_country" className={getInputClass("shipping_country")} type="text" placeholder="United States" value={shippingAddress.country} onChange={(e) => handleShippingChange("country", e.target.value)} />
                            <FieldError message={errors.shipping_country} />
                        </div>
                        <div className="flex flex-col gap-1">
                            <label htmlFor="shipping_zipCode" className="text-xs font-semibold text-gray-600">Zip Code</label>
                            <input id="shipping_zipCode" className={getInputClass("shipping_zipCode")} type="text" placeholder="90001" value={shippingAddress.zipCode} onChange={(e) => handleShippingChange("zipCode", e.target.value)} />
                            <FieldError message={errors.shipping_zipCode} />
                        </div>
                    </>
                )}

                <div className="flex gap-3 mt-4 col-span-1 md:col-span-2 border-t border-gray-100 pt-4">
                    <button type="button" className="flex-1 py-2 bg-gray-100 text-gray-700 font-semibold text-sm rounded-lg hover:bg-gray-200 transition-colors" onClick={() => router.back()}>Cancel</button>
                    <button type="submit" className={`flex-1 py-2 text-white font-semibold text-sm rounded-lg transition-all ${loading ? "bg-gray-400 cursor-not-allowed" : "bg-blue-600 hover:bg-blue-700"}`} disabled={loading}>
                        {loading ? "Processing..." : submitLabel}
                    </button>
                </div>
            </form>
        </div>
    );
}

CustomerForm.propTypes = {
    initialData: PropTypes.object,
    onSubmitAction: PropTypes.func.isRequired,
    submitLabel: PropTypes.string,
    isEdit: PropTypes.bool,
};