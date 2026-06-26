// app/pos/customers/edit/[identifier]/page.jsx

"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import api from "@/app/api/axios";
import { UserCog } from "lucide-react";
import {
    AddressBlock, AccordionSection, AuditTrail, FormFooter, ErrorBanner, SuccessBanner, CustomerPageHeader, CustomerCoreFields,
    CustomerLoadingSkeleton
} from "@/components/customer/CustomerShared";
import { handleSubmitResponse } from "@/components/shared/FormShared";

const PARTY_TYPES = ["RETAIL", "WHOLESALE"];
const BALANCE_TYPES = ["DEBIT", "CREDIT"];

export default function CustomerEditPage() {
    const router = useRouter();
    const params = useParams();
    const identifier = decodeURIComponent(params?.identifier || "");

    const [form, setForm] = useState({
        customerName: "", username: "", partyType: "RETAIL",
        balance: "", balanceType: "DEBIT", creditLimit: "",
    });

    const [billing, setBilling] = useState({ addressLine: "", city: "", state: "", country: "India", zipcode: "" });
    const [shipping, setShipping] = useState({ addressLine: "", city: "", state: "", country: "India", zipcode: "" });
    const [showBilling, setShowBilling] = useState(false);
    const [showShipping, setShowShipping] = useState(false);
    const [audit, setAudit] = useState({ createdBy: null, createdAt: null, modifiedBy: null, modifiedAt: null });
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    useEffect(() => {
        if (!identifier) return;
        (async () => {
            try {
                setLoading(true);
                const res = await api.get("/customer/get", { params: { identifier } });
                const d = res.data;
                setForm({
                    customerName: d.customerName || "",
                    username: d.username || "",
                    partyType: d.partyType || "RETAIL",
                    balance: d.balance ?? "",
                    balanceType: d.balanceType || "DEBIT",
                    creditLimit: d.creditLimit ?? "",
                });
                if (d.billingAddress) { setBilling(d.billingAddress); setShowBilling(true); }
                if (d.shippingAddress) { setShipping(d.shippingAddress); setShowShipping(true); }
                setAudit({
                    createdBy: d.createdBy ?? null,
                    createdAt: d.createdAt ?? null,
                    modifiedBy: d.modifiedBy ?? null,
                    modifiedAt: d.modifiedAt ?? null,
                });
            } catch (err) {
                setError(err.response?.data?.message || "Failed to load customer data.");
            } finally {
                setLoading(false);
            }
        })();
    }, [identifier]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(""); setSuccess(""); setSubmitting(true);
        try {
            const payload = {
                identifier, ...form,
                balance: form.balance === "" ? 0 : Number(form.balance),
                creditLimit: form.creditLimit === "" ? 0 : Number(form.creditLimit),
                ...(showBilling && { billingAddress: billing }),
                ...(showShipping && { shippingAddress: shipping }),
            };
            const res = await api.put("/customer/update", payload);
            handleSubmitResponse({
                res,
                successMessage: "Customer updated successfully! Redirecting...",
                setError,
                setSuccess,
                setAudit,
                router,
            });
        } catch (err) {
            setError(err.response?.data?.message || "Unable to connect to server.");
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <CustomerLoadingSkeleton />;

    return (
        <div className="min-h-screen bg-slate-50 p-6">
            <CustomerPageHeader
                title="Edit Customer"
                subtitle={identifier}
                icon={UserCog}
            />

            <ErrorBanner message={error} />
            <SuccessBanner message={success} />

            <form onSubmit={handleSubmit} className="space-y-6">
                <CustomerCoreFields
                    form={form}
                    onChange={(key, val) => setForm((p) => ({ ...p, [key]: val }))}
                    partyTypes={PARTY_TYPES}
                    balanceTypes={BALANCE_TYPES}
                    identifierField={{
                        label: "Phone Number (Identifier)",
                        value: identifier,
                        disabled: true,
                    }}
                />

                <AccordionSection label="Billing Address" isOpen={showBilling} onToggle={() => setShowBilling((p) => !p)}>
                    <AddressBlock data={billing} onChange={(key, val) => setBilling((p) => ({ ...p, [key]: val }))} title="Billing" />
                </AccordionSection>

                <AccordionSection label="Shipping Address" isOpen={showShipping} onToggle={() => setShowShipping((p) => !p)}>
                    <AddressBlock data={shipping} onChange={(key, val) => setShipping((p) => ({ ...p, [key]: val }))} title="Shipping" />
                </AccordionSection>

                <AuditTrail audit={audit} />

                <FormFooter onCancel={() => router.back()} loading={submitting} submitLabel="Update Customer" />
            </form>
        </div>
    );
}