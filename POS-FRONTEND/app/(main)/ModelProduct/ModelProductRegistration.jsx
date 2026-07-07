"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import { FiBox } from "react-icons/fi";
import CommonAdd from "@/component/CommonAdd";

const ModelProductRegistration = ({ onClose }) => {
  const [formData, setFormData] = useState({
    identifier: "",
    description: "",
    status: true,
  });

  const fields = [
    {
      key: "identifier",
      label: "Model Name",
      type: "text",
      required: true,
    },
    {
      key: "brand",
      label: "Brand",
      type: "search",
      api: "/api/brand/list",
      required: true,
    },
    {
      key: "product",
      label: "Product",
      type: "search",
      api: "/api/product/list",
      required: true,
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
      title="Add Model"
      icon={FiBox}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="model"
      onSubmit={() => onClose()}
      submitLabel="Save Model"
    />
  );
};
ModelProductRegistration.propTypes = {
  model: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    identifier: PropTypes.string,
    description: PropTypes.string,
    status: PropTypes.bool,
  }),
  onClose: PropTypes.func,
};
export default ModelProductRegistration;