"use client";

import BaseEditForm from "@/components/edit/BaseEditForm";

export default function ModelsEditPage() {
    return (
        <BaseEditForm
            title="Model"
            apiPath="models"
            identifierKey="identifier"
        />
    );
}