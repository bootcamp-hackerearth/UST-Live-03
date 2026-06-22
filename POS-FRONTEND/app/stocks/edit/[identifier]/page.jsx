"use client";

import { useEffect, useState } from "react";
import EditFormSkeleton from "@/components/EditSkeleton";
import api from "@/api/axios";

const STATUS_OPTIONS = [
    { value: "AVAILABLE", label: "Available" },
    { value: "OUT_OF_STOCK", label: "Out of Stock" },
    { value: "LOW_STOCK", label: "Low Stock" },
    { value: "INCOMING", label: "Incoming" },
    { value: "BLOCKED", label: "Blocked" },
    { value: "DAMAGED", label: "Damaged" },
];

export default function EditStocks() {
    const [warehouseOptions, setWarehouseOptions] = useState([]);

    useEffect(() => {
        api.get("/wareHouse/findByStatus")
            .then((res) => {
                const list = Array.isArray(res.data) ? res.data : [];
                setWarehouseOptions(list.map((wh) => ({ value: wh.identifier, label: wh.identifier })));
            })
            .catch((err) => {
                if (process.env.NODE_ENV !== "production") console.error(err);
            });
    }, []);

    return (
        <EditFormSkeleton
            title="Stocks"
            apiPath="stocks"
            extraFields={[
                { key: "availableStock", label: "Available Stock", type: "number" },
                { key: "incomingStock", label: "Incoming Stock", type: "number" },
                { key: "outgoingStock", label: "Outgoing Stock", type: "number" },
                { key: "productStatus", label: "Product Status", type: "select", options: STATUS_OPTIONS },
                { key: "wareHouse", label: "Warehouse", type: "select", options: warehouseOptions },
            ]}
        />
    );
}