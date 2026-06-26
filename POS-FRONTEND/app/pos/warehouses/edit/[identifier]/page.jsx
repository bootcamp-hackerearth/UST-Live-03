// app/pos/warehouses/edit/[identifier]/page.jsx

"use client";

import BaseEditForm from "@/components/edit/BaseEditForm";

const EXTRA_FIELDS = [
    { key: "location", label: "Location", type: "text" },
    { key: "manager", label: "Manager", type: "text" },
];

export default function WarehouseEditPage() {
    return (
        <BaseEditForm
            title="Warehouse"
            apiPath="warehouse"
            identifierKey="identifier"
            extraFields={EXTRA_FIELDS}
        />
    );
}