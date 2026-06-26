"use client";

import BaseAddForm from "@/components/add/BaseAddForm";

export default function ShelfAddPage() {
    return (
        <BaseAddForm
            title="Shelf"
            apiPath="shelf"
            identifierKey="identifier"
        />
    );
}