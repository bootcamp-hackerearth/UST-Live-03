"use client";

import AddFormSkeleton from "@/components/AddSkeleton";

export default function AddBrand() {
    return (
        <AddFormSkeleton
            title="Brand"
            apiPath="brand"
            extraFields={[
                { key: "description", label: "Description" },
            ]}
        />
    );
}