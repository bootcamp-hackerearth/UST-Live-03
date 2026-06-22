"use client";

import ListingSkeleton from "@/components/ListingSkeleton";

export default function ListBrand() {
    return (
        <ListingSkeleton
            title="Brand"
            fields={["description"]}
            apis={{
                list: "/brand/list",
                delete: "/brand/delete",
                toggleStatus: "/brand/toggle-status",
            }}
            addPath="/brand/add"
            editPathBase="/brand/edit/"
            deleteStyle="param"
        />
    );
}