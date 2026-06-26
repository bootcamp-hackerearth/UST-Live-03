"use client";

import ListingSkeleton from "../../components/ListingSkeleton";

const BRAND_FIELDS = ["description"];
const BRAND_FIELD_LABELS = { description: "Description" };
const BRAND_APIS = { list: "/brand/list", delete: "/brand/delete", toggleStatus: "/brand/toggle-status" };

export default function ListBrand() {
    return (
        <ListingSkeleton
            title="Brands"
            fields={BRAND_FIELDS}
            fieldLabels={BRAND_FIELD_LABELS}
            apis={BRAND_APIS}
            addPath="/brand/add"
            editPathBase="/brand/edit/"
        />
    );
}