"use client";

import AddFormSkeleton from "../../components/AddFormSkeleton";

export default function AddWareHouse() {
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

    return <AddFormSkeleton title="Warehouse" apiPath="wareHouse" extraFields={wareHouseFields} />;
}