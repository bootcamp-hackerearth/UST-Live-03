"use client";

import { useEffect, useState } from "react";
import AddFormSkeleton from "@/components/AddSkeleton";
import api from "@/api/axios";

const STATUS_OPTIONS = [
    { value: "AVAILABLE", label: "Available" },
    { value: "OUT_OF_STOCK", label: "Out of Stock" },
    { value: "LOW_STOCK", label: "Low Stock" },
    { value: "INCOMING", label: "Incoming" },
    { value: "BLOCKED", label: "Blocked" },
    { value: "DAMAGED", label: "Damaged" },
];

export default function AddStocks() {
    const [productOptions, setProductOptions] = useState([]);
    const [warehouseOptions, setWarehouseOptions] = useState([]);

    useEffect(() => {
        api.get("/product/findByStatus")
            .then((res) => {
                const list = Array.isArray(res.data) ? res.data : [];
                setProductOptions(list.map((p) => ({ value: p.identifier, label: p.identifier })));
            })
            .catch((err) => {
                if (process.env.NODE_ENV !== "production") console.error(err);
            });

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
        <AddFormSkeleton
            title="Stocks"
            apiPath="stocks"
            showIdentifier={false}
            extraFields={[
                { key: "identifier", label: "Identifier (SKU)", type: "select", options: productOptions },
                { key: "availableStock", label: "Available Stock", type: "number" },
                { key: "incomingStock", label: "Incoming Stock", type: "number" },
                { key: "outgoingStock", label: "Outgoing Stock", type: "number" },
                { key: "productStatus", label: "Product Status", type: "select", options: STATUS_OPTIONS },
                { key: "wareHouse", label: "Warehouse", type: "select", options: warehouseOptions },
            ]}
        />
    );
}