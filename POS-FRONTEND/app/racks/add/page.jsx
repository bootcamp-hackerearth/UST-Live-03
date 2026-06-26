"use client";

import CommonAddTemplate from "../../components/CommonAddTemplate";

export default function RacksAdd() {
    const extraFields = [
        {
            key: "shelfs",
            label: "Shelfs",
            type: "multiselect",
            apiEndpoint: "/shelfs/findByStatus",
            required: true,
        },
    ];

    return (
        <CommonAddTemplate
            title="Rack"
            apiPath="racks"
            identifierKey="identifier"
            identifierLabel="Identifier"
            extraFields={extraFields}
            onSuccessPath="/racks"
        />
    );
}