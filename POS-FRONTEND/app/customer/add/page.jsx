"use client";

import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import axiosInstance from "../../api/axiosInstance";
import CustomerForm from "../../components/CustomerForm";

export default function CustomerAddPage() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const redirectTo = searchParams.get("redirectTo") || "/customer";

    const [form, setForm] = useState({
        phoneNo: "",
        name: "",
        email: "",
        billingAddress: { addressLine: "", city: "", state: "", zip: "", country: "" },
        shippingAddress: { addressLine: "", city: "", state: "", zip: "", country: "" },
    });

    const [sameAsShipping, setSameAsShipping] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleChange = (key, value) =>
        setForm((prev) => ({ ...prev, [key]: value }));

    const handleAddressChange = (section, field, value) =>
        setForm((prev) => ({
            ...prev,
            [section]: { ...prev[section], [field]: value },
        }));

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!form.phoneNo.trim() || !form.name.trim() || !form.email.trim()) {
            setError("Phone number, customer name, and email are required.");
            return;
        }

        if (!/^\d{10}$/.test(form.phoneNo)) {
            setError("Please enter a valid 10-digit mobile number.");
            return;
        }

        setLoading(true);
        setError("");

        try {
            const payload = {
                phoneNo: form.phoneNo,
                name: form.name,
                email: form.email,
                billingAddress: sameAsShipping
                    ? { ...form.shippingAddress }
                    : { ...form.billingAddress },
                shippingAddress: { ...form.shippingAddress },
            };

            const res = await axiosInstance.post("/customer/add", payload);

            if (res.data?.success === false) {
                setError(res.data.message || "Failed to add customer.");
                return;
            }

            router.push(redirectTo);
        } catch (err) {
            setError(
                err?.response?.data?.message ||
                err?.response?.data ||
                "Failed to add customer."
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <CustomerForm
            title="Add Customer"
            subtitle="Fill in the details to add a new customer."
            form={form}
            onFieldChange={handleChange}
            onAddressChange={handleAddressChange}
            sameAsShipping={sameAsShipping}
            onSameAsShippingChange={setSameAsShipping}
            onPhoneChange={(val) => handleChange("phoneNo", val)}
            error={error}
            loading={loading}
            onSubmit={handleSubmit}
            onCancel={() => router.back()}
            submitLabel="Save Customer"
        />
    );
}