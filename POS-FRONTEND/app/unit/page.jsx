"use client";

import ListTemplate from "../components/ListTemplate";

export default function UnitList() {
    const columns = [
        {
            label: "Identifier",
            field: "identifier",
        },
        {
            label: "Status",
            field: "status",
        },
    ];

    return (
        <ListTemplate
            title="Unit Management"
            columns={columns}
            urlName="unit"
            showStatus={true}
            editKey="identifier"
            deleteKey="identifier"
            deleteParam="identifier"
            rowKey="identifier"
            addButtonLabel="Unit"
            pageSize={10}
            sortField="identifier"
            editUseQuery={true}
        />
    );
}