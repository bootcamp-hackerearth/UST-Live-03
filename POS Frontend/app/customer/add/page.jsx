"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import api from "@/api/axios";
import SingleDropdown from "@/components/dropdowns/CommonSingleDropdown";
import { useSidebarOpen } from "@/components/useDropdownOptions";
import { CustomerFormFooter } from "@/app/customer/AddressPair";
import {
    C,
    inputSt, inputErrSt, labelSt, errTextSt,
    sectionSt, sectionTitleSt,
    pageSt, containerSt, cardSt,
    headerRowSt, backBtnSt, titleSt, introTextSt,
    alertBoxSt,
    PARTY_TYPE_DROPDOWN_OPTIONS, CREDIT_TYPE_DROPDOWN_OPTIONS,
    EMPTY_ADDRESS, makeFieldSetter, validateCustomerCommon,
} from "@/app/customer/customerShared";

export default function AddCustomer() {
    const router = useRouter();
    const isSidebarOpen = useSidebarOpen();

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

    const setBillingField = makeFieldSetter(setBillingAddress);
    const setShippingField = makeFieldSetter(setShippingAddress);

    function handlePhoneInput(e) {
        setIdentifier(e.target.value.replaceAll(/[^0-9+-]/g, ""));
    }

    function validate() {
        const e = validateCustomerCommon({ customerName, email, partyType, credit, creditType, creditLimit, billingAddress, shippingAddress });
        if (!identifier.trim()) e.identifier = "Phone number is required.";
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
                identifier, customerName, email, partyType,
                credit: Number(credit), creditType, creditLimit: Number(creditLimit),
                billingAddress, shippingAddress,
            });
            const data = res.data;
            if (data?.success === false) { setError(data.message || "Customer already exists."); return; }
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
        <div style={{ ...pageSt, left: isSidebarOpen ? "220px" : "55px" }}>
            <div style={containerSt}>
                <div style={cardSt}>
                    <div style={headerRowSt}>
                        <button type="button" onClick={() => router.back()} style={backBtnSt}>⮜ Back</button>
                        <div>
                            <h2 style={titleSt}>Add Customer</h2>
                            <p style={introTextSt}>Add customer details, credit terms, billing and shipping address in one place.</p>
                        </div>
                    </div>

                    {(error || success) && (
                        <div style={{ ...alertBoxSt, background: error ? C.errorBg : C.greenBg, border: `1px solid ${error ? C.red : C.green}`, color: error ? C.red : C.green }}>
                            {error || success}
                        </div>
                    )}

                    <form onSubmit={handleSubmit}>
                        <div style={sectionSt}>
                            <div style={sectionTitleSt}>Customer Details</div>
                            <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: "16px" }}>
                                <div>
                                    <label style={labelSt} htmlFor="customer-identifier">Phone Number</label>
                                    <input id="customer-identifier" type="text" maxLength={13} inputMode="tel" value={identifier} onChange={handlePhoneInput} style={errors.identifier ? inputErrSt : inputSt} />
                                    {errors.identifier && <span style={errTextSt}>{errors.identifier}</span>}
                                </div>
                                <div>
                                    <label style={labelSt} htmlFor="customer-name">Customer Name</label>
                                    <input id="customer-name" type="text" value={customerName} onChange={(e) => setCustomerName(e.target.value)} style={errors.customerName ? inputErrSt : inputSt} />
                                    {errors.customerName && <span style={errTextSt}>{errors.customerName}</span>}
                                </div>
                                <div>
                                    <label style={labelSt} htmlFor="customer-email">Email</label>
                                    <input id="customer-email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} style={errors.email ? inputErrSt : inputSt} />
                                    {errors.email && <span style={errTextSt}>{errors.email}</span>}
                                </div>
                                <div>
                                    <SingleDropdown label="Party Type" options={PARTY_TYPE_DROPDOWN_OPTIONS} selectedValue={partyType} onChange={setPartyType} />
                                    {errors.partyType && <span style={errTextSt}>{errors.partyType}</span>}
                                </div>
                                <div>
                                    <label style={labelSt} htmlFor="customer-credit">Credit</label>
                                    <input id="customer-credit" type="number" min="0" value={credit} onChange={(e) => setCredit(e.target.value)} style={errors.credit ? inputErrSt : inputSt} />
                                    {errors.credit && <span style={errTextSt}>{errors.credit}</span>}
                                </div>
                                <div>
                                    <SingleDropdown label="Credit Type" options={CREDIT_TYPE_DROPDOWN_OPTIONS} selectedValue={creditType} onChange={setCreditType} />
                                    {errors.creditType && <span style={errTextSt}>{errors.creditType}</span>}
                                </div>
                                <div>
                                    <label style={labelSt} htmlFor="customer-creditLimit">Credit Limit</label>
                                    <input id="customer-creditLimit" type="number" min="0" value={creditLimit} onChange={(e) => setCreditLimit(e.target.value)} style={errors.creditLimit ? inputErrSt : inputSt} />
                                    {errors.creditLimit && <span style={errTextSt}>{errors.creditLimit}</span>}
                                </div>
                            </div>
                        </div>

                        <CustomerFormFooter billingAddress={billingAddress} shippingAddress={shippingAddress} errors={errors} onBillingChange={setBillingField} onShippingChange={setShippingField} loading={loading} onCancel={() => router.back()} submitLabel="Add Customer" loadingLabel="Saving…" />
                    </form>
                </div>
            </div>
        </div>
    );
}
