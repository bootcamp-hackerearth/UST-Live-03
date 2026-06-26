"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import axiosInstance from "../../api/axiosInstance";
import CustomerForm from "../../components/CustomerForm";

export default function CustomerEditPage() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const identifier = searchParams.get("identifier");

    const [form, setForm] = useState({
        phoneNo: "",
        name: "",
        email: "",
        billingAddress: { addressLine: "", city: "", state: "", zip: "", country: "" },
        shippingAddress: { addressLine: "", city: "", state: "", zip: "", country: "" },
    });

    const [sameAsShipping, setSameAsShipping] = useState(false);
    const [loading, setLoading] = useState(false);
    const [pageLoading, setPageLoading] = useState(true);
    const [error, setError] = useState("");
    const [auditData, setAuditData] = useState(null);

    useEffect(() => {
        if (!identifier) return;
        const fetchCustomer = async () => {
            try {
                const res = await axiosInstance.get("/customer/identifier", {
                    params: { identifier },
                });
                const data = res.data || {};
                setForm({
                    phoneNo: data.identifier || data.phoneNo || "",
                    name: data.name || "",
                    email: data.email || "",
                    billingAddress: data.billingAddress || { addressLine: "", city: "", state: "", zip: "", country: "" },
                    shippingAddress: data.shippingAddress || { addressLine: "", city: "", state: "", zip: "", country: "" },
                });
                setAuditData({
                    createdBy: data.createdBy || null,
                    createdAt: data.createdAt || null,
                    modifiedBy: data.modifiedBy || null,
                    modifiedAt: data.modifiedAt || null,
                });
            } catch (err) {
                setError(err?.response?.data?.message || "Failed to load customer.");
            } finally {
                setPageLoading(false);
            }
        };
        fetchCustomer();
    }, [identifier]);

    const handleChange = (key, value) =>
        setForm((prev) => ({ ...prev, [key]: value }));

    const handleAddressChange = (section, field, value) =>
        setForm((prev) => ({
            ...prev,
            [section]: { ...prev[section], [field]: value },
        }));

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError("");
        try {
            const payload = {
                identifier: form.phoneNo,
                name: form.name,
                email: form.email,
                billingAddress: sameAsShipping
                    ? { ...form.shippingAddress }
                    : { ...form.billingAddress },
                shippingAddress: { ...form.shippingAddress },
            };
            const res = await axiosInstance.post("/customer/update", payload);
            if (res.data?.success === false) {
                setError(res.data.message || "Failed to update customer.");
                return;
            }
            router.push("/customer");
        } catch (err) {
            setError(
                err?.response?.data?.message ||
                err?.response?.data ||
                "Failed to update customer."
            );
        } finally {
            setLoading(false);
        }
    };

    if (pageLoading) {
        return (
            <div className="mx-auto flex w-full max-w-3xl items-center justify-center py-20">
                <div className="text-sm font-semibold text-slate-500">Loading...</div>
            </div>
        );
    }

    return (
        <CustomerForm
            title="Update Customer"
            subtitle="Update the details below."
            form={form}
            onFieldChange={handleChange}
            onAddressChange={handleAddressChange}
            sameAsShipping={sameAsShipping}
            onSameAsShippingChange={setSameAsShipping}
            phoneDisabled
            error={error}
            loading={loading}
            onSubmit={handleSubmit}
            onCancel={() => router.back()}
            submitLabel="Update Customer"
            auditData={auditData}
        />
    );
}