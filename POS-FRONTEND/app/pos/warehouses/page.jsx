// app/pos/warehouses/page.jsx

"use client";

import BaseListForm from "@/components/lists/BaseListForm";

const COLUMNS = [
    { key: "identifier", label: "Warehouse ID" },
    { key: "location", label: "Location" },
    { key: "manager", label: "Manager" },
    { key: "status", label: "Status" },
];

export default function WarehouseListPage() {
    return (
        <BaseListForm
            title="Warehouses"
            entity="warehouse"
            columns={COLUMNS}
            addPath="/pos/warehouses/add"
            editPath="/pos/warehouses/edit"
            identifierKey="identifier"
        />
    );
}