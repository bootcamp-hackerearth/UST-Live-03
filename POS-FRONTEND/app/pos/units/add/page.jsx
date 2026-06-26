// app/pos/units/add/page.jsx

"use client";

import BaseAddForm from "@/components/add/BaseAddForm";

export default function UnitAddPage() {
    return (
        <BaseAddForm
            title="Unit"
            apiPath="unit"
            identifierKey="identifier"
        />
    );
}