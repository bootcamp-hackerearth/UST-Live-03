"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import AddFormSkeleton from "../../components/AddFormSkeleton";
import SingleDropDown from "../../components/SingleDropDown";

function ProductDropdownWrapper({ identifier, setIdentifier }) {
    return (
        <SingleDropDown
            label="Product"
            apiUrl="/product/findByStatus"
            valueField="identifier"
            labelField="identifier"
            selectedValue={identifier}
            onChange={setIdentifier}
        />
    );
}

ProductDropdownWrapper.propTypes = {
    identifier: PropTypes.string.isRequired,
    setIdentifier: PropTypes.func.isRequired,
};

function WarehouseDropdownWrapper({ wareHouse, setWareHouse }) {
    return (
        <SingleDropDown
            label="Warehouse"
            apiUrl="/wareHouse/findByStatus"
            valueField="identifier"
            labelField="identifier"
            selectedValue={wareHouse}
            onChange={setWareHouse}
        />
    );
}

WarehouseDropdownWrapper.propTypes = {
    wareHouse: PropTypes.string.isRequired,
    setWareHouse: PropTypes.func.isRequired,
};

export default function AddStock() {
    const [identifier, setIdentifier] = useState("");
    const [wareHouse, setWareHouse] = useState("");

    const stockFields = [
        {
            key: "identifier",
            type: "custom",
            label: "Product",
            component: (
                <ProductDropdownWrapper
                    identifier={identifier}
                    setIdentifier={setIdentifier}
                />
            ),
        },
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
            component: (
                <WarehouseDropdownWrapper
                    wareHouse={wareHouse}
                    setWareHouse={setWareHouse}
                />
            ),
        },
    ];

    return (
        <AddFormSkeleton
            title="Stock"
            apiPath="stocks"
            extraFields={stockFields}
            extraData={{ identifier, wareHouse }}
        />
    );
}