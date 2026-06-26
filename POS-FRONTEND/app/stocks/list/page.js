"use client";

import ListingSkeleton from "../../components/ListingSkeleton";

const STOCK_FIELDS = ["name", "availableStock", "outgoingStock", "productStatus", "wareHouse"];

const STOCK_FIELD_LABELS = {
    name: "Product Name",
    availableStock: "Available Stock",
    outgoingStock: "Outgoing Stock",
    productStatus: "Product Status",
    wareHouse: "Warehouse",
};

const STOCK_APIS = {
    list: "/stocks/list",
    delete: "/stocks/delete",
    toggleStatus: "/stocks/toggle-status",
};

export default function ListStock() {
    return (
        <ListingSkeleton
            title="Stocks"
            fields={STOCK_FIELDS}
            fieldLabels={STOCK_FIELD_LABELS}
            apis={STOCK_APIS}
            addPath="/stocks/add"
            editPathBase="/stocks/edit/"
        />
    );
}