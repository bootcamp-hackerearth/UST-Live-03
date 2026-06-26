"use client";

import ListingSkeleton from "../../components/ListingSkeleton";

const CUSTOMER_FIELDS = ["customerName", "email", "partyType", "creditType", "credit", "creditLimit"];

const CUSTOMER_FIELD_LABELS = {
    customerName: "Customer Name",
    email: "Email",
    partyType: "Party Type",
    creditType: "Credit Type",
    credit: "Credit",
    creditLimit: "Credit Limit",
};

const CUSTOMER_APIS = {
    list: "/customer/list",
    delete: "/customer/delete",
    toggleStatus: "/customer/toggle-status",
};

export default function ListCustomer() {
    return (
        <ListingSkeleton
            title="Customers"
            fields={CUSTOMER_FIELDS}
            fieldLabels={CUSTOMER_FIELD_LABELS}
            apis={CUSTOMER_APIS}
            addPath="/customer/add"
            editPathBase="/customer/edit/"
        />
    );
}