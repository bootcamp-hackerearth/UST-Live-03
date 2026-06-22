"use client";

import AddFormSkeleton from "@/components/AddSkeleton";

export default function AddWarehouse() {
    return (
        <AddFormSkeleton
            title="Warehouse"
            apiPath="wareHouse"
            extraFields={[
                { key: "location", label: "Location" },
                { key: "manager", label: "Manager" },
            ]}
        />
    );
}