"use client";

import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { FiTag } from "react-icons/fi";
import CommonEdit from "@/component/CommonEdit";

const BrandEdit = ({ brand, onClose }) => {
  const [formData, setFormData] = useState({
    id: "",
    identifier: "",
    description: "",
    status: true,
  });

  useEffect(() => {
    if (brand) {
      setFormData({
        id: brand.id,
        identifier: brand.identifier || "",
        description: brand.description || "",
        status: brand.status ?? true,
      });
    }
  }, [brand]);

  const fields = [
    {
      key: "identifier",
      label: "Brand Name",
      type: "text",
      disabled: true,
    },
    {
      key: "description",
      label: "Description",
      type: "text",
    },
    {
      key: "status",
      label: "Status",
      type: "checkbox",
    },
  ];

  return (
    <CommonEdit
      title="Edit Brand"
      icon={FiTag}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="brand"
      onSubmit={() => onClose()}
      submitLabel="Update Brand"
    />
  );
};

BrandEdit.propTypes = {
  brand: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    identifier: PropTypes.string,
    description: PropTypes.string,
    status: PropTypes.bool,
  }),
  onClose: PropTypes.func.isRequired,
};
export default BrandEdit;
