"use client";

import React from "react";
import CommonList from "@/components/CommonList";
import { idColumn, statusColumn, identifierColumn, identifierField, statusField } from "@/components/entityHelpers";
import { requiredValidation } from "@/validation/validation";

export default function ModelsPage() {
    const columns = [idColumn, identifierColumn("Model Code"), statusColumn];

    const fields = [identifierField("Enter Model Code", { validation: requiredValidation }), statusField({ validation: requiredValidation })];

    return (
        <CommonList
            title="Models"
            subtitle="Manage terminal layout models and configurations"
            apiRoute="models"
            columns={columns}
            searchKeys={["identifier"]}
            fields={fields}
            dropdownApis={{}}
        />
    );
}