"use client";

import React from "react";
import CommonList from "@/components/CommonList";
import { requiredValidation, emailValidation, nameValidation, phoneValidation } from "@/validation/validation"
import { statusColumn, identifierField, statusField, addressFields } from "@/components/entityHelpers";

export default function CustomersPage() {
    const columns = [
        {
            key: "id",
            label: "ID",
        },
        {
            key: "identifier",
            label: "Customer Code",
        },
        {
            key: "name",
            label: "Full Name",
        },
        {
            key: "phoneNo",
            label: "Phone Number",
        },
        {
            key: "userType",
            label: "Customer Type",
        },
        {
            key: "balance",
            label: "Balance",
            render: (item) => <span>₹{Number(item.balance || 0).toFixed(2)}</span>,
        },
        {
            key: "creditLimit",
            label: "Credit Limit",
            render: (item) => <span>₹{Number(item.creditLimit || 0).toFixed(2)}</span>,
        },
        statusColumn,
    ];

    const fields = [
        identifierField("Enter Customer Code / Email", { validation: emailValidation }),
        {
            name: "name",
            type: "text",
            placeholder: "Enter Full Name",
            hardCoded: "false",
            hardCodedArray: [],
            readOnly: false,
            validation: nameValidation,
        },
        {
            name: "phoneNo",
            type: "number",
            placeholder: "Enter 10-Digit Phone Number",
            hardCoded: "false",
            hardCodedArray: [],
            readOnly: false,
            validation: phoneValidation,
        },
        {
            name: "userType",
            type: "select",
            placeholder: "Select Customer Type",
            hardCoded: "true",
            multiple: false,
            hardCodedArray: ["customer", "premium", "admin"],
            readOnly: false,
            validation: requiredValidation,
        },
        {
            name: "balance",
            type: "number",
            placeholder: "Enter Initial Balance",
            hardCoded: "false",
            hardCodedArray: [],
            readOnly: false,
        },
        {
            name: "creditLimit",
            type: "number",
            placeholder: "Enter Credit Limit",
            hardCoded: "false",
            hardCodedArray: [],
            readOnly: false,
        },
        ...addressFields("shippingAddress", { required: true, validation: requiredValidation, phoneValidation }),
        ...addressFields("billingAddress", { required: true, validation: requiredValidation, phoneValidation, types: ["billingAddress"] }),
        statusField({ validation: requiredValidation }),
    ];

    return (
        <CommonList
            title="Customers"
            subtitle="Manage terminal client master profile configurations"
            apiUrl="http://localhost:8080/api/customer/list"
            deleteUrl="http://localhost:8080/api/customer/delete"
            apiRoute="customer"
            columns={columns}
            searchKeys={["identifier", "name", "phoneNo", "userType"]}
            fields={fields}
            dropdownApis={{}}
        />
    );
}