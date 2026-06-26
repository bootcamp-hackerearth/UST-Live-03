"use client";

import ListTemplate from "../components/ListTemplate";

export default function ModelList() {
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
            title="Model Management"
            columns={columns}
            urlName="model"
            showStatus={true}
            editKey="identifier"
            deleteKey="identifier"
            deleteParam="identifier"
            rowKey="identifier"
            addButtonLabel="Model"
            pageSize={10}
            sortField="identifier"
            editUseQuery={true}
        />
    );
}