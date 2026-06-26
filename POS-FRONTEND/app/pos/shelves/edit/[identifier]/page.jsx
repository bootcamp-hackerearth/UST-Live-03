"use client";

import BaseEditForm from "@/components/edit/BaseEditForm";

export default function ShelfEditPage() {
    return (
        <BaseEditForm
            title="Shelf"
            apiPath="shelf"
            identifierKey="identifier"
        />
    );
}