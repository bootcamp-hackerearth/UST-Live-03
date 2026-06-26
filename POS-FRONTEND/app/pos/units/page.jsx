// app/pos/units/page.jsx

"use client";

import BaseListForm from "@/components/lists/BaseListForm";

const COLUMNS = [
    { key: "identifier", label: "Units" },
    { key: "status", label: "Status" },
];

export default function UnitListPage() {
    return (
        <BaseListForm
            title="Units"
            entity="unit"
            columns={COLUMNS}
            addPath="/pos/units/add"
            editPath="/pos/units/edit"
            identifierKey="identifier"
        />
    );
}