"use client";

import ListTemplate from "../components/ListTemplate";

export default function BrandList() {
    const columns = [
        {
            label: "Identifier",
            field: "identifier",
        },
        {
            label: "Description",
            field: "description",
        },
        {
            label: "Status",
            field: "status",
        },
    ];

    return (
        <ListTemplate
            title="Brand Management"
            columns={columns}
            urlName="brand"
            showStatus={true}
            editKey="identifier"
            deleteKey="identifier"
            deleteParam="identifier"
            rowKey="identifier"
            addButtonLabel="Brand"
            pageSize={10}
            sortField="identifier"
            editUseQuery={true}
        />
    );
}