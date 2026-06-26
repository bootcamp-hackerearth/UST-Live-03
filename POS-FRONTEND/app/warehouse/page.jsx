"use client";

import ListTemplate from "../components/ListTemplate";

export default function WarehouseList() {
    const columns = [
        {
            label: "Identifier",
            field: "identifier",
        },
        {
            label: "Code",
            field: "code",
        },
        {
            label: "Location",
            field: "location",
        },
        {
            label: "Status",
            field: "status",
        },
    ];

    return (
        <ListTemplate
            title="Warehouse Management"
            columns={columns}
            urlName="warehouse"
            showStatus={true}
            editKey="identifier"
            deleteKey="identifier"
            deleteParam="identifier"
            rowKey="identifier"
            addButtonLabel="Warehouse"
            pageSize={10}
            sortField="identifier"
            editUseQuery={true}
        />
    );
}