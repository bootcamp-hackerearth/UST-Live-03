"use client";

import ListTemplate from "../components/ListTemplate";

export default function RacksList() {
    const columns = [
        {
            label: "Identifier",
            field: "identifier",
        },
        {
            label: "Shelfs",
            field: "shelfs",
            render: (item) => {
                if (!item.shelfs) return "-";

                if (Array.isArray(item.shelfs)) {
                    return item.shelfs.join(", ");
                }
                return String(item.shelfs).replaceAll(",", ", ");
            },
        },
        {
            label: "Status",
            field: "status",
        },
    ];

    return (
        <ListTemplate
            title="Racks Management"
            columns={columns}
            urlName="racks"
            showStatus={true}
            editKey="identifier"
            deleteKey="identifier"
            deleteParam="identifier"
            rowKey="identifier"
            addButtonLabel="Rack"
            pageSize={10}
            sortField="identifier"
            editUseQuery={true}
        />
    );
}