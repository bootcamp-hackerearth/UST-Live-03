"use client";

import AddEditForm from "@/components/AddEditForm";
import { getCustomerFields } from "../../customerFields";

export default function CustomerEdit() {
    return (
        <AddEditForm
            title="Customer"
            fields={getCustomerFields(true)}
            apiRoute="customer"
            dropdownApis={{}}
            method="update"
        />
    );
}