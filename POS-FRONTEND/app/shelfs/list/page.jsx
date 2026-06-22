"use client";

import ListingSkeleton from "@/components/ListingSkeleton";

export default function ListShelfs() {
    return (
        <ListingSkeleton
            title="Shelfs"
            fields={[]}
            apis={{
                list: "/shelfs/list",
                delete: "/shelfs/delete",
                toggleStatus: "/shelfs/toggle-status",
            }}
            addPath="/shelfs/add"
            editPathBase="/shelfs/edit/"
            deleteStyle="param"
        />
    );
}