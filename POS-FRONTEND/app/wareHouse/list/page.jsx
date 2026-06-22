"use client";

import ListingSkeleton from "@/components/ListingSkeleton";

export default function ListWarehouse() {
    return (
        <ListingSkeleton
            title="Warehouse"
            fields={["location", "manager"]}
            apis={{
                list: "/wareHouse/list",
                delete: "/wareHouse/delete",
                toggleStatus: "/wareHouse/toggle-status",
            }}
            addPath="/wareHouse/add"
            editPathBase="/wareHouse/edit/"
            deleteStyle="param"
        />
    );
}