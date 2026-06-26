"use client";

import CommonAddTemplate from "../../components/CommonAddTemplate";

export default function BrandAdd() {
    const extraFields = [
        {
            key: "description",
            label: "Description",
            type: "text",
            placeholder: "Enter description",
            required: false,
        },
    ];

    return (
        <CommonAddTemplate
            title="Brand"
            apiPath="brand"
            identifierKey="identifier"
            identifierLabel="Identifier"
            extraFields={extraFields}
            onSuccessPath="/brand"
        />
    );
}