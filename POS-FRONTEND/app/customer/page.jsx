"use client";
import ListTemplate from "../components/ListTemplate";

function formatAuditDate(value) {
    if (!value) return "-";
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return String(value);
    return new Intl.DateTimeFormat("en-IN", {
        dateStyle: "medium",
        timeStyle: "short",
    }).format(date);
}

export default function CustomerList() {
    const columns = [
        { label: "Phone Number", field: "identifier" },
        { label: "Customer Name", field: "name" },
        { label: "Email", field: "email" },
        { label: "Status", field: "status" },
    ];

    return (
        <ListTemplate
            title="Customer Management"
            columns={columns}
            urlName="customer"
            showStatus={true}
            editKey="identifier"
            deleteKey="identifier"    
            deleteParam="identifier"  
            rowKey="identifier"
            addButtonLabel="Customer"
            pageSize={10}
            sortField="identifier"
            editUseQuery={true}
        />
    );
}