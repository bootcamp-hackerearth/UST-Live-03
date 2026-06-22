"use client";

import ListingSkeleton from "@/components/ListingSkeleton";

export default function ListRacks() {
    return (
        <ListingSkeleton
            title="Racks"
            fields={["shelfs"]}
            apis={{
                list: "/racks/list",
                delete: "/racks/delete",
                toggleStatus: "/racks/toggle-status",
            }}
            addPath="/racks/add"
            editPathBase="/racks/edit/"
            deleteStyle="param"
        />
    );
}