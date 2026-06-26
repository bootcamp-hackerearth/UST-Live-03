"use client";

import ListingSkeleton from "../../components/ListingSkeleton";

const RACK_FIELDS = ["shelfs"];
const RACK_FIELD_LABELS = { shelfs: "Shelfs" };
const RACK_APIS = { list: "/racks/list", delete: "/racks/delete", toggleStatus: "/racks/toggle-status" };

export default function ListRacks() {
    return (
        <ListingSkeleton
            title="Racks"
            fields={RACK_FIELDS}
            fieldLabels={RACK_FIELD_LABELS}
            apis={RACK_APIS}
            addPath="/racks/add"
            editPathBase="/racks/edit/"
        />
    );
}