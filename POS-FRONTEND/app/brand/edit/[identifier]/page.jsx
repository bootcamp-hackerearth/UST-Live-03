"use client";

import EditFormSkeleton from "@/components/EditSkeleton";

export default function EditBrand() {
    return (
        <EditFormSkeleton
            title="Brand"
            apiPath="brand"
            extraFields={[
                { key: "description", label: "Description" },
            ]}
        />
    );
}