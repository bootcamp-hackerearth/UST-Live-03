"use client";

import ListingSkeleton from "@/components/ListingSkeleton";

export default function ListCustomer() {
    return (
        <ListingSkeleton
            title="Customer"
            fields={[
                "customerName",
                "email",
                "partyType",
                "creditType",
                "credit",
                "creditLimit",
            ]}
            apis={{
                list: "/customer/list",
                delete: "/customer/delete",
                toggleStatus: "/customer/toggle-status",
            }}
            addPath="/customer/add"
            editPathBase="/customer/edit/"
            deleteStyle="param"
            identifierLabel="Phone Number"
        />
    );
}