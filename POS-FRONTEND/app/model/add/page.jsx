"use client";

import CommonAddTemplate from "../../components/CommonAddTemplate";

export default function ModelAdd() {
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
            title="Model"
            apiPath="model"
            identifierKey="identifier"
            identifierLabel="Identifier"
            extraFields={extraFields}
            onSuccessPath="/model"
        />
    );
}