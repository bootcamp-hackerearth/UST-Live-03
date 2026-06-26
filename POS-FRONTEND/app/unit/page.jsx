"use client";

import React from "react";
import CommonList from "@/components/CommonList";
import { idColumn, statusColumn, identifierColumn, identifierField, statusField } from "@/components/entityHelpers";
import { requiredValidation } from "@/validation/validation";

export default function UnitsPage() {
    const columns = [idColumn, identifierColumn("Unit Code"), statusColumn];
    const fields = [identifierField("Enter Unit Code", { validation: requiredValidation }), statusField({ validation: requiredValidation })];
    return (
        <CommonList
            title="Units"
            subtitle="Manage terminal layout inventory measurement units"
            apiUrl="http://localhost:8080/api/unit/list"
            deleteUrl="http://localhost:8080/api/unit/delete"
            apiRoute="unit"
            columns={columns}
            searchKeys={["identifier"]}
            fields={fields}
            dropdownApis={{}}
        />
    );
}