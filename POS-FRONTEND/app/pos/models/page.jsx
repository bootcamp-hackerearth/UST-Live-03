"use client";

import BaseListForm from "@/components/lists/BaseListForm";

const COLUMNS = [
    { key: "identifier", label: "Model ID" },
    { key: "status", label: "Status" },
];

export default function ModelsListPage() {
    return (
        <BaseListForm
            title="Models"
            entity="models"
            columns={COLUMNS}
            addPath="/pos/models/add"
            editPath="/pos/models/edit"
            identifierKey="identifier"
        />
    );
}