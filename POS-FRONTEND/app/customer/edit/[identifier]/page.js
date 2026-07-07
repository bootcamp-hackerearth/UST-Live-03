"use client";
import { useEffect, useState, use } from "react";
import PropTypes from "prop-types";
import Layout from "@/app/components/Layout";
import CustomerForm from "../../customerForm";

async function handleEditSubmit(payload, setSuccess, setError) {
    const token = (globalThis.window == "undefined") ? null : localStorage.getItem("token");
    
    const res = await fetch("/api/customer/update", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            ...(token && { "Authorization": `Bearer ${token}` }),
        },
        body: JSON.stringify(payload),
    });
    const data = await res.json();

    if (!res.ok) {
        setError(data.message || "Failed updating user specifications record profile metrics.");
        return;
    }
    setSuccess(data.message || "Customer metadata updated successfully!");
}

export default function EditCustomerPage({ params }) {
    const resolvedParams = use(params);
    const { identifier } = resolvedParams;
    
    const [customerData, setCustomerData] = useState(null);
    const [fetchError, setFetchError] = useState("");
    useEffect(() => {
                async function fetchCustomer() {
            try {
                const token = (globalThis.window == "undefined") ? null : localStorage.getItem("token");
                const res = await fetch(`/api/customer/get?identifier=${identifier}`, {
                    method: "GET",
                    headers: { 
                        ...(token && { "Authorization": `Bearer ${token}` }) 
                    }
                });
                
                if (!res.ok) {
                    setFetchError(`Backend returned error status: ${res.status}`);
                    return;
                }

                const data = await res.json();
                if (data) {
                    setCustomerData(data);
                } else {
                    setFetchError("Customer record not found.");
                }
            } catch (err) {
                console.error(err);
                setFetchError("Unable to connect to backend server. Verify network/CORS configurations.");
            }
        }
        if (identifier) fetchCustomer();
    }, [identifier]);

    return (
        <Layout>
            <div className="w-full h-screen min-h-screen bg-[#f9fafb] font-sans overflow-y-auto antialiased">
                <div className="w-full min-h-full p-4 md:p-8 flex items-start justify-center">
                    {fetchError ? (
                        <div className="bg-red-50 text-red-700 p-4 rounded-lg border border-red-200 text-sm max-w-md text-center shadow-sm">
                            <p className="font-semibold mb-1">Data Retrieval Failure</p>
                            <p className="text-xs text-red-600">{fetchError}</p>
                        </div>
                    ) : (
                        <CustomerForm 
                            initialData={customerData} 
                            onSubmitAction={handleEditSubmit} 
                            submitLabel="Update Customer" 
                            isEdit={true} 
                        />
                    )}
                </div>
            </div>
        </Layout>
    );
}

EditCustomerPage.propTypes = {
    params: PropTypes.shape({
        identifier: PropTypes.string,
    }).isRequired,
};