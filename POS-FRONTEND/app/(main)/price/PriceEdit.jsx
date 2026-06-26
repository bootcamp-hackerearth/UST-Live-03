"use client";

import { useState, useEffect } from "react";
import { FiEdit } from "react-icons/fi";
import CommonEdit from "@/component/CommonEdit";
import PropTypes from "prop-types";

const PriceEdit = ({ price, onClose }) => {

    const [formData, setFormData] = useState({
        identifier: "",
        costPrice: "",
        sellingPrice: ""
    });

    useEffect(() => {

        if (price) {

            setFormData({
                identifier: price.identifier || "",
                costPrice: price.costPrice || "",
                sellingPrice: price.sellingPrice || ""
            });
        }

    }, [price]);

    const fields = [
        {
            key: "identifier",
            label: "Product",
            type: "text",
            disabled: true
        },
        {
            key: "costPrice",
            label: "Cost Price",
            type: "number"
        },
        {
            key: "sellingPrice",
            label: "Selling Price",
            type: "number"
        }
    ];

    return (
        <CommonEdit
            title="Edit Price"
            icon={FiEdit}
            formData={formData}
            setFormData={setFormData}
            fields={fields}
            moduleName="price"
            onSubmit={() => { onClose(); }}
            submitLabel="Update Price"
        />
    );
};
PriceEdit.propTypes = {
    price: PropTypes.shape({
        identifier: PropTypes.string,
        costPrice: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ]),
        sellingPrice: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ])
    }),
    onClose: PropTypes.func.isRequired
};
export default PriceEdit;