"use client";

import CommonAddTemplate from "../../components/CommonAddTemplate";

export default function WarehouseAdd() {
    const extraFields = [
        {
            key: "code",
            label: "Code",
            type: "text",
            placeholder: "Enter code",
            required: true,
        },
        {
            key: "location",
            label: "Location",
            type: "text",
            placeholder: "Enter location",
            required: true,
        },
        {
            key: "status",
            label: "Status",
            type: "select",
            options: [
                { value: "true", label: "Active" },
                { value: "false", label: "Inactive" },
            ],
            valueType: "boolean",
            required: true,
        },
    ];

    return (
        <CommonAddTemplate
            title="Warehouse"
            apiPath="warehouse"
            identifierKey="identifier"
            identifierLabel="Identifier"
            extraFields={extraFields}
            onSuccessPath="/warehouse"
        />
    );
}