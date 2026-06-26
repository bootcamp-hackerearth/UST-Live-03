"use client";

import CommonAddTemplate from "../../components/CommonAddTemplate";

export default function UnitAdd() {
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
            title="Unit"
            apiPath="unit"
            identifierKey="identifier"
            identifierLabel="Identifier"
            extraFields={extraFields}
            onSuccessPath="/unit"
        />
    );
}