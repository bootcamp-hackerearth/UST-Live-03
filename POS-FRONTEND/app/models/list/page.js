"use client";

import ListingSkeleton from "../../components/ListingSkeleton";

const MODEL_FIELDS = [];
const MODEL_APIS = { list: "/models/list", delete: "/models/delete", toggleStatus: "/models/toggle-status" };

export default function ListModels() {
    return (
        <ListingSkeleton
            title="Models"
            fields={MODEL_FIELDS}
            apis={MODEL_APIS}
            addPath="/models/add"
            editPathBase="/models/edit/"
        />
    );
}