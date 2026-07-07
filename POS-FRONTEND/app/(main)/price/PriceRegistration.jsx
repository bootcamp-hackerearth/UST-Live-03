"use client";

import { useState } from "react";
import { FiDollarSign } from "react-icons/fi";
import CommonAdd from "@/component/CommonAdd";
import PropTypes from "prop-types";

const PriceRegistration = ({ onClose }) => {

    const [formData, setFormData] = useState({
        identifier: "",
        costPrice: "",
        sellingPrice: ""
    });

    const fields = [
      {
        key: "identifier",
        label: "Product",
        type: "search",
        api: "/api/product/list",
        filterFunction: (item) => item.status === true,
        required: true,
      },
      {
        key: "costPrice",
        label: "MRP",
        type: "number",
        required: true,
      },
      {
        key: "sellingPrice",
        label: "Selling Price",
        type: "number",
        required: true,
      },
    ];

    return (
        <CommonAdd
            title="Add Price"
            icon={FiDollarSign}
            formData={formData}
            setFormData={setFormData}
            fields={fields}
            moduleName="price"
            onSubmit={() => { onClose(); }}
            submitLabel="Add Price"
        />
    );
};
PriceRegistration.propTypes = {
    onClose: PropTypes.func.isRequired
};
export default PriceRegistration;