"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import {
    inputSt,
    inputErrSt,
    EMPTY_ADDRESS,
    buildErrors,
    FormField,
    CustomerDetailsSection,
    AddressGrid,
    PageHeader,
    AlertBanner,
    FormActions,
    pageWrapSt,
    pageInnerSt,
    useCustomerMutation,
} from "../_shared/customerForm";

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

    const fieldValues = { customerName, email, partyType, credit, creditType, creditLimit };
    const fieldSetters = {
        customerName: setCustomerName,
        email: setEmail,
        partyType: setPartyType,
        credit: setCredit,
        creditType: setCreditType,
        creditLimit: setCreditLimit,
    };

    const { loading, error, success, submitMutation } = useCustomerMutation({
        endpoint: "/customer/add",
        method: "post",
        defaultErrorMsg: "Customer already exists.",
        successMsg: "Customer added successfully",
        router,
    });

    function handlePhoneInput(e) {
        setIdentifier(e.target.value.replaceAll(/[^0-9+-]/g, ""));
    }
    function handleFieldChange(key, val) {
        fieldSetters[key](val);
    }
    function setBillingField(key, val) {
        setBillingAddress((prev) => ({ ...prev, [key]: val }));
    }
    function setShippingField(key, val) {
        setShippingAddress((prev) => ({ ...prev, [key]: val }));
    }

    function validate() {
        const e = buildErrors({ identifier, ...fieldValues, billingAddress, shippingAddress });
        setErrors(e);
        return Object.keys(e).length === 0;
    }

    async function handleSubmit(e) {
        e.preventDefault();
        if (!validate()) return;

        await submitMutation({
            identifier,
            ...fieldValues,
            credit: Number(credit),
            creditLimit: Number(creditLimit),
            billingAddress,
            shippingAddress,
        });
    }

    return (
        <div style={pageWrapSt}>
            <div style={pageInnerSt}>
                <PageHeader title="Add Customer" onBack={() => router.back()} />
                <AlertBanner error={error} success={success} />
                <form onSubmit={handleSubmit}>
                    <CustomerDetailsSection
                        values={fieldValues}
                        errors={errors}
                        onChange={handleFieldChange}
                        extra={(
                            <FormField id="identifier" label="Phone Number" error={errors.identifier}>
                                <input
                                    id="identifier"
                                    type="text"
                                    maxLength={13}
                                    inputMode="tel"
                                    value={identifier}
                                    onChange={handlePhoneInput}
                                    style={errors.identifier ? inputErrSt : inputSt}
                                />
                            </FormField>
                        )}
                    />
                    <AddressGrid
                        billingAddress={billingAddress}
                        shippingAddress={shippingAddress}
                        onBillingChange={setBillingField}
                        onShippingChange={setShippingField}
                        errors={errors}
                    />
                    <FormActions
                        onCancel={() => router.back()}
                        loading={loading}
                        submitLabel="Add Customer"
                        savingLabel="Saving…"
                    />
                </form>
            </div>
        </div>
    );
}