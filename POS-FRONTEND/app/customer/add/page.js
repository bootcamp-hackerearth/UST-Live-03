"use client";
import Layout from "@/app/components/Layout";
import CustomerForm from "../../customer/customerForm";

async function handleAddSubmit(payload, setSuccess, setError) {
    const token = (globalThis.window == "undefined") ? null : localStorage.getItem("token");
    const res = await fetch("http://localhost:8080/api/customer/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            ...(token && { "Authorization": `Bearer ${token}` }),
        },
        body: JSON.stringify(payload),
    });
    const data = await res.json();
    
    if (!res.ok) {
        if (res.status === 403) throw new Error("Authorization failed. Your session might be expired.");
        setError(data.message || "Server error response");
        return;
    }
    if (data.success === false) {
        setError(data.message || "Customer already exists.");
        return;
    }
    setSuccess(data.message || "Customer added successfully!");
}

export default function AddCustomerPage() {
    return (
        <Layout>
            <div className="w-full h-screen min-h-screen bg-[#f9fafb] font-sans overflow-y-auto antialiased">
                <div className="w-full min-h-full p-4 md:p-8 flex items-start justify-center">
                    <CustomerForm onSubmitAction={handleAddSubmit} submitLabel="Save Customer" isEdit={false} />
                </div>
            </div>
        </Layout>
    );
}