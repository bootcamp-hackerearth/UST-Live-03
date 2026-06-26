"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import { FiTag } from "react-icons/fi";
import CommonAdd from "@/component/CommonAdd";

const BrandRegistration = ({ onClose }) => {
  const [formData, setFormData] = useState({
    identifier: "",
    description: "",
    status: true,
  });

  const fields = [
    {
      key: "identifier",
      label: "Brand Name",
      type: "text",
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
    <CommonAdd
      title="Brand"
      icon={FiTag}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="brand"
      submitLabel="Save Brand"
      onSubmit={() => onClose()}
    />
  );
};

BrandRegistration.propTypes = {
  onClose: PropTypes.func.isRequired,
};

export default BrandRegistration;
