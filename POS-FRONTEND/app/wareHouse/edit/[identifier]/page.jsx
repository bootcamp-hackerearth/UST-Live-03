"use client";

import EditFormSkeleton from "@/components/EditSkeleton";

export default function EditWarehouse() {
    return (
        <EditFormSkeleton
            title="Warehouse"
            apiPath="wareHouse"
            extraFields={[
                { key: "location", label: "Location" },
                { key: "manager", label: "Manager" },
            ]}
        />
    );
}