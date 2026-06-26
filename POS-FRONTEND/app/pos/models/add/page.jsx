"use client";

import BaseAddForm from "@/components/add/BaseAddForm";

export default function ModelsAddPage() {
    return (
        <BaseAddForm
            title="Model"
            apiPath="models"
            identifierKey="identifier"
        />
    );
}