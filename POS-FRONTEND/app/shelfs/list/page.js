"use client";

import ListingSkeleton from "../../components/ListingSkeleton";

const SHELF_FIELDS = [];
const SHELF_APIS = { list: "/shelfs/list", delete: "/shelfs/delete", toggleStatus: "/shelfs/toggle-status" };

export default function ListShelfs() {
    return (
        <ListingSkeleton
            title="Shelves"
            fields={SHELF_FIELDS}
            apis={SHELF_APIS}
            addPath="/shelfs/add"
            editPathBase="/shelfs/edit/"
        />
    );
}