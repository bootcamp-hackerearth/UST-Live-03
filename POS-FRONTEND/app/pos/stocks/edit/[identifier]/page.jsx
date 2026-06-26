// app/pos/stocks/edit/[identifier]/page.jsx

"use client";

import { useEffect, useState } from "react";
import BaseEditForm from "@/components/edit/BaseEditForm";
import api from "@/app/api/axios";

const PRODUCT_STATUS_OPTIONS = [
    { label: "Active", value: "ACTIVE" },
    { label: "Inactive", value: "INACTIVE" },
    { label: "Out of Stock", value: "OUT_OF_STOCK" },
    { label: "Low Stock", value: "LOW_STOCK" },
];

export default function StockEditPage() {
    const [warehouseOptions, setWarehouseOptions] = useState([]);
    const [loadingOptions, setLoadingOptions] = useState(true);

    useEffect(() => {
        api.get("/warehouse/getAllActive")
            .then((res) => {
                const list = Array.isArray(res.data) ? res.data : res.data?.dtoList || [];
                setWarehouseOptions(
                    list.map((w) => ({
                        label: `${w.identifier}${w.location ? " · " + w.location : ""}`,
                        value: w.identifier,
                    }))
                );
            })
            .catch(() => setWarehouseOptions([]))
            .finally(() => setLoadingOptions(false));
    }, []);

    const extraFields = [
        {
            key: "availableStock",
            label: "Available Stock",
            type: "number",
        },
        {
            key: "outgoingStock",
            label: "Outgoing Stock",
            type: "number",
        },
        {
            key: "warehouse",
            label: "Warehouse",
            type: "select",
            options: warehouseOptions,
        },
        {
            key: "productStatus",
            label: "Product Status",
            type: "select",
            options: PRODUCT_STATUS_OPTIONS,
        },
    ];

    if (loadingOptions) {
        return (
            <div className="min-h-screen bg-white p-6 flex items-center justify-center">
                <p className="text-sm text-gray-400 animate-pulse">Loading warehouse options...</p>
            </div>
        );
    }

    return (
        <BaseEditForm
            title="Stock"
            apiPath="stock"
            identifierKey="identifier"
            extraFields={extraFields}
        />
    );
}