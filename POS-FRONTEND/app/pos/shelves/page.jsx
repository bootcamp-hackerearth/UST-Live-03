"use client";

import BaseListForm from "@/components/lists/BaseListForm";

const COLUMNS = [
    { key: "identifier", label: "Shelf ID" },
    { key: "status", label: "Status" },
];

export default function ShelfListPage() {
    return (
        <BaseListForm
            title="Shelves"
            entity="shelf"
            columns={COLUMNS}
            addPath="/pos/shelves/add"
            editPath="/pos/shelves/edit"
            identifierKey="identifier"
        />
    );
}