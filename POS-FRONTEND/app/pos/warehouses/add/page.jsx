// app/pos/warehouses/add/page.jsx

"use client";

import BaseAddForm from "@/components/add/BaseAddForm";

const EXTRA_FIELDS = [
    { key: "location", label: "Location", type: "text" },
    { key: "manager", label: "Manager", type: "text" },
];

export default function WarehouseAddPage() {
    return (
        <BaseAddForm
            title="Warehouse"
            apiPath="warehouse"
            identifierKey="identifier"
            extraFields={EXTRA_FIELDS}
        />
    );
}