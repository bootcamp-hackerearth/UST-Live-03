"use client";

import AddFormSkeleton from "../../components/AddFormSkeleton";

export default function AddBrand() {
    const brandFields = [
        {
            key: "description",
            label: "Description",
            type: "text",
        },
    ];

    return <AddFormSkeleton title="Brand" apiPath="brand" extraFields={brandFields} />;
}