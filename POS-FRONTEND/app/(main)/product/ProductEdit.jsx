"use client";

import { useState, useEffect } from "react";
import { FiLayers } from "react-icons/fi";
import PropTypes from "prop-types";
import CommonEdit from "@/component/CommonEdit";
const ProductEdit = ({ product, onClose }) => {

    const [formData, setFormData] = useState({
        identifier: "",
        category: "",
        warehouseName: "",
        supplierId: "",
        status: true
    });

    useEffect(() => {
        if (product) {
            setFormData(product);
        }
    }, [product]);

    const productFields = [
        {
            key: "identifier",
            label: "Identifier",
            type: "text",
            disabled: true
        },
        {
            key: "category",
            label: "Category",
            type: "search",
            api: "/api/category/list"
        },
        {
            key: "warehouseName",
            label: "Warehouse Name",
            type: "search",
            api: "/api/warehouse/list"
        },
        {
            key: "supplierId",
            label: "Supplier ID",
            type: "number"
        }
    ];

    return (
        <CommonEdit
            title="Edit Product"
            icon={FiLayers}
            formData={formData}
            setFormData={setFormData}
            fields={productFields}
            moduleName="product"
            onSubmit={() => { onClose(); }}
            submitLabel="Update Product"
        />
    );
};
ProductEdit.propTypes = {
    onClose: PropTypes.func.isRequired,
    product: PropTypes.shape({
        identifier: PropTypes.string,
        category: PropTypes.string,
        warehouseName: PropTypes.string,
        supplierId: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ]),
        status: PropTypes.bool
    })
};
export default ProductEdit;