"use client";

import EditFormSkeleton from "../../../components/EditFormSkeleton";

export default function EditWareHouse() {
    const wareHouseFields = [
        {
            key: "location",
            label: "Location",
            type: "text",
        },
        {
            key: "manager",
            label: "Manager",
            type: "text",
        },
    ];

    return <EditFormSkeleton title="Warehouse" apiPath="wareHouse" extraFields={wareHouseFields} />;
}