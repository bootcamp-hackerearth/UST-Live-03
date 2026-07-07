"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import api from "@/api/axios";
import {
    C,
    inputReadOnlySt,
    labelSt,
    EMPTY_ADDRESS,
    buildErrors,
    CustomerDetailsSection,
    AddressGrid,
    AuditFooter,
    PageHeader,
    AlertBanner,
    FormActions,
    pageWrapSt,
    pageInnerSt,
    useCustomerMutation,
} from "../../_shared/customerForm";

export default function EditCustomer() {
    const router = useRouter();
    const params = useParams();
    const identifier = params?.identifier ?? "";

    const [customerName, setCustomerName] = useState("");
    const [email, setEmail] = useState("");
    const [partyType, setPartyType] = useState("");
    const [credit, setCredit] = useState("");
    const [creditType, setCreditType] = useState("");
    const [creditLimit, setCreditLimit] = useState("");
    const [billingAddress, setBillingAddress] = useState({ ...EMPTY_ADDRESS });
    const [shippingAddress, setShippingAddress] = useState({ ...EMPTY_ADDRESS });
    const [auditInfo, setAuditInfo] = useState({ createdBy: "", createdAt: "", modifiedBy: "", modifiedAt: "" });
    const [errors, setErrors] = useState({});
    const [fetching, setFetching] = useState(true);

    const fieldValues = { customerName, email, partyType, credit, creditType, creditLimit };
    const fieldSetters = {
        customerName: setCustomerName,
        email: setEmail,
        partyType: setPartyType,
        credit: setCredit,
        creditType: setCreditType,
        creditLimit: setCreditLimit,
    };

    const { loading, error, setError, success, submitMutation } = useCustomerMutation({
        endpoint: "/customer/update",
        method: "put",
        defaultErrorMsg: "Failed to update customer.",
        successMsg: "Customer updated successfully",
        router,
    });

    useEffect(() => {
        if (!identifier) return;

        Promise.all([
            api.get("/customer/get", { params: { identifier } }),
            api.get("/address/findByAllPhoneNo", { params: { phoneNo: identifier } }),
        ])
            .then(([customerRes, addressRes]) => {
                const d = customerRes.data;
                const addresses = addressRes.data ?? [];

                const billing = addresses.find((a) => a.addressType === "Billing");
                const shipping = addresses.find((a) => a.addressType === "Shipping");

                setCustomerName(d.customerName ?? "");
                setEmail(d.email ?? "");
                setPartyType(d.partyType ?? "");
                setCredit(d.credit ?? "");
                setCreditType(d.creditType ?? "");
                setCreditLimit(d.creditLimit ?? "");
                setBillingAddress({ ...EMPTY_ADDRESS, ...billing });
                setShippingAddress({ ...EMPTY_ADDRESS, ...shipping });
                setAuditInfo({
                    createdBy: d.createdBy ?? "",
                    createdAt: d.createdAt ?? "",
                    modifiedBy: d.modifiedBy ?? "",
                    modifiedAt: d.modifiedAt ?? "",
                });
            })
            .catch((err) => {
                if (process.env.NODE_ENV !== "production") console.error(err);
                setError("Failed to load customer data.");
            })
            .finally(() => setFetching(false));
    }, [identifier, setError]);

    function handleFieldChange(key, val) {
        fieldSetters[key](val);
    }
    function setBillingField(key, val) { setBillingAddress((prev) => ({ ...prev, [key]: val })); }
    function setShippingField(key, val) { setShippingAddress((prev) => ({ ...prev, [key]: val })); }

    function validate() {
        const e = buildErrors({ ...fieldValues, billingAddress, shippingAddress });
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

    if (fetching) {
        return (
            <div style={{ padding: "60px", textAlign: "center", color: C.muted, fontFamily: "'Segoe UI', sans-serif" }}>
                Loading customer details…
            </div>
        );
    }

    return (
        <div style={pageWrapSt}>
            <div style={pageInnerSt}>
                <PageHeader title="Edit Customer" onBack={() => router.back()} />
                <AlertBanner error={error} success={success} />
                <form onSubmit={handleSubmit}>
                    <CustomerDetailsSection
                        values={fieldValues}
                        errors={errors}
                        onChange={handleFieldChange}
                        extra={(
                            <div>
                                <label htmlFor="identifier" style={labelSt}>Phone Number</label>
                                <input id="identifier" type="text" value={identifier} readOnly style={inputReadOnlySt} />
                            </div>
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
                        submitLabel="Update Customer"
                        savingLabel="Updating…"
                    />
                </form>
                <AuditFooter
                    createdBy={auditInfo.createdBy}
                    createdAt={auditInfo.createdAt}
                    modifiedBy={auditInfo.modifiedBy}
                    modifiedAt={auditInfo.modifiedAt}
                />
            </div>
        </div>
    );
}