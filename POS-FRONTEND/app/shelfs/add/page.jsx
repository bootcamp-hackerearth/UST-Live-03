"use client";

import CommonAddTemplate from "../../components/CommonAddTemplate";

export default function ShelfsAdd() {
    const extraFields = [
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
            title="Shelfs"
            apiPath="shelfs"
            identifierKey="identifier"
            identifierLabel="Identifier"
            extraFields={extraFields}
            onSuccessPath="/shelfs"
        />
    );
}