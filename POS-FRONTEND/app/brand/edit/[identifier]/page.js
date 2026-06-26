"use client";

import EditFormSkeleton from "../../../components/EditFormSkeleton";

export default function EditBrand() {
    const brandFields = [
        {
            key: "description",
            label: "Description",
            type: "text",
        },
    ];

    return <EditFormSkeleton title="Brand" apiPath="brand" extraFields={brandFields} />;
}