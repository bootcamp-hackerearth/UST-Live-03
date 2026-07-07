"use client";
import PropTypes from 'prop-types';
import { useState } from "react";
import { FiPackage } from "react-icons/fi";
import CommonAdd from "@/component/CommonAdd";

const ProductRegistration = ({ onSuccess, onClose }) => {
  const [productData, setProductData] = useState({
    identifier: '',
    supplierId: '',
    warehouseName: '',
    category: '',
  });

  const fields = [
    {
      key: "identifier",
      label: "Product Name",
      type: "text",
      required: true,
    },
    {
      key: "supplierId",
      label: "Supplier ID",
      type: "number",
      required: true,
    },
    {
      key: "warehouseName",
      label: "Warehouse",
      type: "search",
      api: "/api/warehouse/list",
      required: true,
    },
    {
      key: "category",
      label: "Category",
      type: "search",
      api: "/api/category/list",
      required: true,
    },
    {
      key: "unit",
      label: "Unit",
      type: "search",
      api: "/api/unit/list",
      required: true,
    },
  ];

  return (
    <CommonAdd
      title="Add Product"
      icon={FiPackage}
      formData={productData}
      setFormData={setProductData}
      fields={fields}
      moduleName="product"
      onSubmit={() => {
        if (onSuccess) onSuccess();
        if (onClose) onClose();
      }}
      submitLabel="Add Product"
    />
  );
};
ProductRegistration.propTypes = {
  onClose: PropTypes.func,
  onSuccess: PropTypes.func
};
export default ProductRegistration;