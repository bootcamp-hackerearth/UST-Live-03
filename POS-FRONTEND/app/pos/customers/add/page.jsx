// app/pos/customers/add/page.jsx

"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import api from "@/app/api/axios";
import { UserPlus } from "lucide-react";
import { AddressBlock, AccordionSection, FormFooter, ErrorBanner, SuccessBanner, CustomerPageHeader, CustomerCoreFields }
    from "@/components/customer/CustomerShared";

const PARTY_TYPES = ["RETAIL", "LOYALTY MEMBER", "WHOLESALER", "DEALER"];
const BALANCE_TYPES = ["DEBIT", "CREDIT"];

export default function CustomerAddPage() {
    const router = useRouter();

    const [form, setForm] = useState({
        identifier: "", customerName: "", username: "",
        partyType: "RETAIL", balance: "", balanceType: "DEBIT", creditLimit: "",
    });

    const [billing, setBilling] = useState({ addressLine: "", city: "", state: "", country: "India", zipcode: "" });
    const [shipping, setShipping] = useState({ addressLine: "", city: "", state: "", country: "India", zipcode: "" });
    const [showBilling, setShowBilling] = useState(false);
    const [showShipping, setShowShipping] = useState(false);
    const [sameAsBilling, setSameAsBilling] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const handleSameAsBilling = (checked) => {
        setSameAsBilling(checked);
        if (checked) setShipping({ ...billing });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(""); setSuccess("");
        if (!form.identifier || !form.customerName || !form.username) {
            setError("Phone number, full name, and email are required.");
            return;
        }
        setLoading(true);
        try {
            const payload = {
                ...form,
                balance: form.balance === "" ? 0 : Number(form.balance),
                creditLimit: form.creditLimit === "" ? 0 : Number(form.creditLimit),
                ...(showBilling && { billingAddress: billing }),
                ...(showShipping && { shippingAddress: sameAsBilling ? billing : shipping }),
            };
            const res = await api.post("/customer/add", payload);
            if (res.data?.success === false) { setError(res.data.message || "Failed to add customer."); return; }
            setSuccess("Customer added successfully! Redirecting...");
            setTimeout(() => router.back(), 1500);
        } catch (err) {
            setError(err.response?.data?.message || "Unable to connect to server.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 p-6">
            <CustomerPageHeader
                title="Add Customer"
                subtitle="Fill in the details to create a new customer"
                icon={UserPlus}
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
                        label: "Phone Number",
                        value: form.identifier,
                        disabled: false,
                        onChange: (val) => setForm((p) => ({ ...p, identifier: val })),
                        placeholder: "10-digit mobile number",
                        required: true,
                    }}
                />

                <AccordionSection label="Billing Address (optional)" isOpen={showBilling} onToggle={() => setShowBilling((p) => !p)}>
                    <AddressBlock
                        data={billing}
                        onChange={(key, val) => {
                            setBilling((p) => ({ ...p, [key]: val }));
                            if (sameAsBilling) setShipping((p) => ({ ...p, [key]: val }));
                        }}
                        title="Billing"
                    />
                </AccordionSection>

                <AccordionSection label="Shipping Address (optional)" isOpen={showShipping} onToggle={() => setShowShipping((p) => !p)}>
                    <label className="flex items-center gap-2 mt-4 mb-2 cursor-pointer text-xs font-semibold text-gray-500">
                        <input
                            type="checkbox"
                            checked={sameAsBilling}
                            onChange={(e) => handleSameAsBilling(e.target.checked)}
                            className="accent-[#006E74]"
                        />
                        <span>Same as billing address</span>
                    </label>
                    {!sameAsBilling && (
                        <AddressBlock
                            data={shipping}
                            onChange={(key, val) => setShipping((p) => ({ ...p, [key]: val }))}
                            title="Shipping"
                        />
                    )}
                </AccordionSection>

                <FormFooter onCancel={() => router.back()} loading={loading} submitLabel="Add Customer" />
            </form>
        </div>
    );
}