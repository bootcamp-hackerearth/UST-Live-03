"use client";

import React from "react";
import CommonList from "@/components/CommonList";
import { requiredValidation, nameValidation, phoneValidation, } from "@/validation/validation";

export default function WarehousePage() {
    const columns = [
        {
            key: "id",
            label: "ID",
        },
        {
            key: "identifier",
            label: "Warehouse Name",
        },
        {
            key: "location",
            label: "Location",
        },
        {
            key: "contactName",
            label: "Contact Name",
        },
        {
            key: "contactNumber",
            label: "Contact Number",
        },
        {
            key: "country",
            label: "Country",
        },
        {
            key: "region",
            label: "Region",
        },
    ];

    const fields = [
        {
            name: "identifier",
            type: "text",
            placeholder: "Enter Warehouse Name",
            hardCoded: "false",
            hardCodedArray: [],
            readOnly: false,
            validation: requiredValidation
        },
        {
            name: "location",
            type: "text",
            placeholder: "Enter Location",
            hardCoded: "false",
            hardCodedArray: [],
            validation: requiredValidation,
            readOnly: false,
        },
        {
            name: "contactName",
            type: "text",
            placeholder: "Enter Contact Name",
            hardCoded: "false",
            hardCodedArray: [],
            validation: nameValidation,
            readOnly: false,
        },
        {
            name: "contactNumber",
            type: "text",
            placeholder: "Enter Contact Number",
            hardCoded: "false",
            hardCodedArray: [],
            readOnly: false,
            validation: phoneValidation
        },
        {
            name: "country",
            type: "text",
            placeholder: "Enter Country",
            hardCoded: "false",
            hardCodedArray: [],
            validation: requiredValidation,
            readOnly: false,
        },
        {
            name: "region",
            type: "text",
            placeholder: "Enter Region",
            hardCoded: "false",
            hardCodedArray: [],
            required: true,
            readOnly: false,
        },
    ];

    return (
        <CommonList
            title="Warehouses"
            subtitle="Manage logistical inventory fulfillment centers and deployment stations"
            apiUrl="http://localhost:8080/api/warehouse/list"
            deleteUrl="http://localhost:8080/api/warehouse/delete"
            apiRoute="warehouse"
            columns={columns}
            searchKeys={["identifier", "location", "contactName", "contactNumber", "country", "region"]}
            fields={fields}
            dropdownApis={{}}
        />
    );
}