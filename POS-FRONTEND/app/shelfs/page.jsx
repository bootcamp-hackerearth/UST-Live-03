"use client";

import ListTemplate from "../components/ListTemplate";

export default function ShelfsList() {
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
            title="Shelfs Management"
            columns={columns}
            urlName="shelfs"
            showStatus={true}
            editKey="identifier"
            deleteKey="identifier"
            deleteParam="identifier"
            rowKey="identifier"
            addButtonLabel="Shelfs"
            pageSize={10}
            sortField="identifier"
            editUseQuery={true}
        />
    );
}