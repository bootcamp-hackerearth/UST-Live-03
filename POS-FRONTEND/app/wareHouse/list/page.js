"use client";

import ListingSkeleton from "../../components/ListingSkeleton";

const WAREHOUSE_FIELDS = ["location", "manager"];
const WAREHOUSE_FIELD_LABELS = { location: "Location", manager: "Manager" };
const WAREHOUSE_APIS = { list: "/wareHouse/list", delete: "/wareHouse/delete", toggleStatus: "/wareHouse/toggle-status" };

export default function ListWareHouse() {
    return (
        <ListingSkeleton
            title="Warehouses"
            fields={WAREHOUSE_FIELDS}
            fieldLabels={WAREHOUSE_FIELD_LABELS}
            apis={WAREHOUSE_APIS}
            addPath="/wareHouse/add"
            editPathBase="/wareHouse/edit/"
        />
    );
}