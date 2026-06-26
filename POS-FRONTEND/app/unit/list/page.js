"use client";

import ListingSkeleton from "../../components/ListingSkeleton";

const UNIT_FIELDS = [];
const UNIT_APIS = { list: "/unit/list", delete: "/unit/delete", toggleStatus: "/unit/toggle-status" };

export default function ListUnit() {
    return (
        <ListingSkeleton
            title="Units"
            fields={UNIT_FIELDS}
            apis={UNIT_APIS}
            addPath="/unit/add"
            editPathBase="/unit/edit/"
        />
    );
}