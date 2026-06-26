// app/pos/units/edit/[identifier]/page.jsx

"use client";

import BaseEditForm from "@/components/edit/BaseEditForm";

export default function UnitEditPage() {
    return (
        <BaseEditForm
            title="Unit"
            apiPath="unit"
            identifierKey="identifier"
        />
    );
}