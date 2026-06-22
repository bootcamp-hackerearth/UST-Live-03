"use client";

import ListingSkeleton from "@/components/ListingSkeleton";

export default function ListStocks() {
    return (
        <ListingSkeleton
            title="Stocks"
            fields={[
                "name",
                "availableStock",
                "incomingStock",
                "outgoingStock",
                "wareHouse",
                "productStatus",
            ]}
            apis={{
                list: "/stocks/list",
                delete: "/stocks/delete",
                toggleStatus: "/stocks/toggle-status",
            }}
            addPath="/stocks/add"
            editPathBase="/stocks/edit/"
            deleteStyle="param"
        />
    );
}