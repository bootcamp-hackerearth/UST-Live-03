"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import EditFormSkeleton from "../../../components/EditFormSkeleton";
import SingleDropDown from "../../../components/SingleDropDown";

function WarehouseDropdownWrapper({ value, onChange }) {
    return (
        <SingleDropDown
            label="Warehouse"
            apiUrl="/wareHouse/findByStatus"
            valueField="identifier"
            labelField="identifier"
            selectedValue={value}
            onChange={onChange}
        />
    );
}

WarehouseDropdownWrapper.propTypes = {
    value: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired,
};

export default function EditStock() {
    const [wareHouse, setWareHouse] = useState("");

    const stockFields = [
        {
            key: "availableStock",
            label: "Available Stock",
            type: "number",
        },
        {
            key: "outgoingStock",
            label: "Outgoing Stock",
            type: "number",
        },
        {
            key: "productStatus",
            label: "Product Status",
            type: "select",
            options: [
                { value: "AVAILABLE", label: "Available" },
                { value: "OUT_OF_STOCK", label: "Out of Stock" },
                { value: "DISCONTINUED", label: "Discontinued" },
            ],
        },
        {
            key: "wareHouse",
            type: "custom",
            label: "Warehouse",
            CustomComponent: WarehouseDropdownWrapper,
        },
        {
            key: "name",
            label: "Product Name",
            type: "text",
        },
    ];

    return (
        <EditFormSkeleton
            title="Stock"
            apiPath="stocks"
            extraFields={stockFields}
            setters={{ wareHouse: setWareHouse }}
            externalExtraData={{ wareHouse }}
        />
    );
}