"use client";

import AddEditForm from "@/components/AddEditForm";
import { getCustomerFields } from "../customerFields";

export default function CustomerAdd() {
    return (
        <AddEditForm
            title="Customer"
            fields={getCustomerFields(false)}
            apiRoute="customer"
            dropdownApis={{}}
            method="add"
        />
    );
}