// app/pos/stocks/page.jsx

"use client";

import BaseListForm from "@/components/lists/BaseListForm";

// 1. Unified and cleanly mapped styles matching Tailwind conventions
const STATUS_STYLES = {
    ACTIVE:       "bg-emerald-50 text-emerald-700 border-emerald-200",
    INACTIVE:     "bg-gray-100 text-gray-500 border-gray-200",
    OUT_OF_STOCK: "bg-rose-50 text-rose-600 border-rose-200",
    LOW_STOCK:    "bg-amber-50 text-amber-700 border-amber-200",
};

const COLUMNS = [
    { key: "identifier", label: "Product ID" },
    { key: "warehouse", label: "Warehouse" },
    {
        key: "availableStock",
        label: "Available",
        render: (value) => {
            const num = Number(value);
            let textClass = "text-[#006E74]";
            if (num <= 0) textClass = "text-rose-500";
            else if (num <= 10) textClass = "text-amber-500";
            return (
                <span className={`font-bold text-sm ${textClass}`}>
                    {value ?? "—"}
                </span>
            );
        },
    },
    {
        key: "outgoingStock",
        label: "Outgoing",
        render: (value) => (
            <span className="font-semibold text-gray-500">{value ?? "—"}</span>
        ),
    },
    {
        key: "productStatus",
        label: "Stock Status",
        render: (value) => {
            if (!value) return <span className="text-gray-300 text-xs">—</span>;
            
            const lookupKey = String(value).toUpperCase().trim();
            const tailwindBadgeStyles = STATUS_STYLES[lookupKey] || "bg-gray-100 text-gray-600 border-gray-200";
            
            const readableText = lookupKey
                .toLowerCase()
                .split('_')
                .map(word => word.charAt(0).toUpperCase() + word.slice(1))
                .join(' ');

            return (
                <span className={`inline flex items-center whitespace-nowrap text-[11px] font-semibold px-3 py-1 rounded-full border tracking-wide whitespace-nowrap ${tailwindBadgeStyles}`}>
                    {readableText}
                </span>
            );
        },
    },
    { key: "status", label: "Status" },
];

export default function StockListPage() {
    return (
        <BaseListForm
            title="Stock"
            entity="stock"
            columns={COLUMNS}
            addPath="/pos/stocks/add"
            editPath="/pos/stocks/edit"
            identifierKey="identifier"
        />
    );
}