"use client";

import React from "react";
import CommonList from "@/components/CommonList";
import { idColumn, statusColumn, identifierColumn, identifierField, statusField } from "@/components/entityHelpers";
import { requiredValidation, nameValidation } from "@/validation/validation";

export default function BrandsPage() {
    const columns = [idColumn, identifierColumn("Brand Code"), { key: "description", label: "Description" }, statusColumn];

    const fields = [
        identifierField("Enter Brand Name", { validation: nameValidation }),
        {
            name: "description",
            type: "text",
            placeholder: "Enter Description",
            hardCoded: "false",
            hardCodedArray: [],
            required: true,
            readOnly: false,
            validation: requiredValidation,
        },
        statusField({ validation: requiredValidation }),
    ];

    return (
        <CommonList
            title="Brands"
            subtitle="Manage global point-of-sale inventory brand profiles"
            apiRoute="brand"
            columns={columns}
            searchKeys={["identifier", "description"]}
            fields={fields}
            dropdownApis={{}}
        />
    );
}