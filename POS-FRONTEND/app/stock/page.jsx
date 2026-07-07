"use client";

import React from "react";
import CommonList from "@/components/CommonList";
import { idColumn, statusColumn, identifierColumn, identifierField, statusField } from "@/components/entityHelpers";
import { requiredValidation } from "@/validation/validation";

export default function StockPage() {
    const columns = [
        idColumn,
        identifierColumn("Stock Code"),
        { key: "product", label: "Product ID" },
        { key: "warehouse", label: "Warehouse Location" },
        { key: "quantity", label: "Quantity", render: (item) => <span className="font-mono">{item.quantity ?? 0}</span> },
        statusColumn,
    ];

    const fields = [
        identifierField("Enter Stock Code", { validation: requiredValidation }),
        {
            name: "product",
            type: "select",
            placeholder: "Select Product Reference",
            dataKey: "products",
            hardCoded: "false",
            multiple: false,
            hardCodedArray: [],
            readOnly: false,
            validation: requiredValidation,
        },
        {
            name: "warehouse",
            type: "text",
            placeholder: "Enter Warehouse Location Name",
            hardCoded: "false",
            hardCodedArray: [],
            validation: requiredValidation,
            readOnly: false,
        },
        {
            name: "quantity",
            type: "number",
            placeholder: "Enter Stock Quantity",
            hardCoded: "false",
            hardCodedArray: [],
            validation: requiredValidation,
            readOnly: false,
        },
        statusField({ validation: requiredValidation }),
    ];

    const dropdownApis = {
        products: process.env.NEXT_PUBLIC_BASE_URL+"/product/list",
    };

    return (
        <CommonList
            title="Stock"
            subtitle="Track terminal batch inventory volume indexes across warehouse positions"
            apiRoute="stock"
            columns={columns}
            searchKeys={["identifier", "product", "warehouse"]}
            fields={fields}
            dropdownApis={dropdownApis}
        />
    );
}