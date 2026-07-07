"use client";

import React from "react";
import CommonList from "@/components/CommonList";
import { idColumn, statusColumn, identifierColumn, identifierField, statusField } from "@/components/entityHelpers";
import { requiredValidation } from "@/validation/validation";

export default function ShelvesPage() {
    const columns = [idColumn, identifierColumn("Shelf Code"), statusColumn];

    const fields = [identifierField("Enter Shelf Code", { validation: requiredValidation }), statusField({ validation: requiredValidation })];

    return (
        <CommonList
            title="Shelves"
            subtitle="Manage inventory physical storage units and layout locations"
            apiRoute="shelf"
            columns={columns}
            searchKeys={["identifier"]}
            fields={fields}
            dropdownApis={{}}
        />
    );
}