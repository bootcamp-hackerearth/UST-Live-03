"use client";

import BaseListForm from "@/components/lists/BaseListForm";

const COLUMNS = [
    {
        key: "iconPath",
        label: "Brand Icon",
        render: (value) =>
            value ? (
                <img
                    src={`http://localhost:8080/uploads/${value}`}
                    alt="brand icon"
                    className="w-8 h-8 rounded-lg object-contain bg-gray-50 border border-gray-100"
                />
            ) : (
                <div className="w-8 h-8 rounded-lg bg-gray-100 flex items-center justify-center text-gray-300 text-xs">—</div>
            ),
    },
    { key: "identifier", label: "Brand ID" },
    { key: "description", label: "Description" },
    { key: "status", label: "Status" },
];

export default function BrandListPage() {
    return (
        <BaseListForm
            title="Brands"
            entity="brand"
            columns={COLUMNS}
            addPath="/pos/brands/add"
            editPath="/pos/brands/edit"
            identifierKey="identifier"
        />
    );
}