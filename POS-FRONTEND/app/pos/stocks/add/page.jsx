// app/pos/stocks/add/page.jsx

"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import api from "@/app/api/axios";
import SingleDropdown from "@/components/dropDowns/singleDropDown";

const PRODUCT_STATUS_OPTIONS = [
    { label: "Active", value: "ACTIVE" },
    { label: "Inactive", value: "INACTIVE" },
    { label: "Out of Stock", value: "OUT_OF_STOCK" },
    { label: "Low Stock", value: "LOW_STOCK" },
];

export default function StockAddPage() {
    const router = useRouter();

    const [form, setForm] = useState({
        identifier: "",
        availableStock: "",
        outgoingStock: "",
        warehouse: "",
        productStatus: "ACTIVE",
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const set = (key, value) => setForm((p) => ({ ...p, [key]: value }));

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");
        if (!form.identifier) { setError("Please select a product."); return; }
        if (!form.warehouse) { setError("Please select a warehouse."); return; }
        setLoading(true);
        try {
            const parseNonNegativeNumber = (v) => {
                const n = Number(v);
                if (!Number.isFinite(n) || n < 0) return 0;
                return Math.trunc(n);
            };

            const payload = {
                identifier: form.identifier,
                availableStock: parseNonNegativeNumber(form.availableStock),
                outgoingStock: parseNonNegativeNumber(form.outgoingStock),
                warehouse: form.warehouse,
                productStatus: form.productStatus,
            };
            const res = await api.post("/stock/add", payload);
            if (res.data?.success === false) { setError(res.data.message || "Failed to add stock."); return; }
            setSuccess("Stock added successfully");
            setTimeout(() => router.back(), 1500);
        } catch (err) {
            setError(err.response?.data?.message || "Unable to connect to server.");
        } finally {
            setLoading(false);
        }
    };

    const fieldClass = "mt-1 w-full px-4 py-2 border rounded-md text-sm border-[#006E74]/30 focus:border-[#006E74] focus:ring-1 focus:ring-[#006E74] outline-none bg-white";
    const labelClass = "text-xs font-semibold text-[#006E74] uppercase";

    return (
        <div className="min-h-screen bg-white p-6">
            <div className="mb-6">
                <h1 className="text-2xl font-bold text-[#231F20]">Add Stock</h1>
                <p className="text-sm text-[#0097AC] mt-1">Fill in the details below</p>
            </div>

            {error && (
                <div role="alert" className="mb-4 px-4 py-3 border text-sm font-medium rounded-md bg-white text-red-600 border-red-300">
                    {error}
                </div>
            )}
            {success && (
                <output className="block mb-4 px-4 py-3 border text-sm font-medium rounded-md bg-white text-green-600 border-green-300">
                    {success}
                </output>
            )}

            <form onSubmit={handleSubmit} className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">

                    <SingleDropdown
                        label="Product"
                        entity="product"
                        selectedValue={form.identifier}
                        onChange={(val) => set("identifier", val)}
                        valueField="identifier"
                        labelField="identifier"
                    />

                    <div>
                        <label htmlFor="available-stock" className={labelClass}>Available Stock</label>
                        <input
                            id="available-stock"
                            type="number"
                            value={form.availableStock}
                            onChange={(e) => set("availableStock", e.target.value)}
                            placeholder="Enter available stock"
                            min="0"
                            step="1"
                            className={fieldClass}
                        />
                    </div>

                    <div>
                        <label htmlFor="outgoing-stock" className={labelClass}>Outgoing Stock</label>
                        <input
                            id="outgoing-stock"
                            type="number"
                            value={form.outgoingStock}
                            onChange={(e) => set("outgoingStock", e.target.value)}
                            placeholder="Enter outgoing stock"
                            min="0"
                            step="1"
                            className={fieldClass}
                        />
                    </div>

                    <SingleDropdown
                        label="Warehouse"
                        entity="warehouse"
                        selectedValue={form.warehouse}
                        onChange={(val) => set("warehouse", val)}
                        valueField="identifier"
                        labelField="identifier"
                    />

                    <div>
                        <label htmlFor="product-status" className={labelClass}>Product Status</label>
                        <select
                            id="product-status"
                            value={form.productStatus}
                            onChange={(e) => set("productStatus", e.target.value)}
                            className={fieldClass}
                        >
                            {PRODUCT_STATUS_OPTIONS.map((s) => (
                                <option key={s.value} value={s.value}>{s.label}</option>
                            ))}
                        </select>
                    </div>
                </div>

                <div className="sticky bottom-0 bg-white pt-4 border-t border-[#006E74]/20 flex justify-end gap-3">
                    <button
                        type="button"
                        onClick={() => router.back()}
                        className="px-6 py-2 text-sm border rounded-md text-[#231F20] border-[#006E74]/30 hover:bg-gray-50 transition-colors"
                    >
                        Cancel
                    </button>
                    <button
                        type="submit"
                        disabled={loading}
                        className={`px-6 py-2 text-sm rounded-md text-white transition-colors ${loading ? "bg-[#006E74]/50 cursor-not-allowed" : "bg-[#006E74] hover:bg-[#0097AC]"
                            }`}
                    >
                        {loading ? "Saving..." : "Add Stock"}
                    </button>
                </div>
            </form>
        </div>
    );
}