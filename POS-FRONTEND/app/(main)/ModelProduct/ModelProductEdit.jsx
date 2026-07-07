"use client";

import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { FiBox } from "react-icons/fi";
import CommonEdit from "@/component/CommonEdit";

const ModelProductEdit = ({ model, onClose }) => {
  const [formData, setFormData] = useState({
    id: "",
    identifier: "",
    description: "",
    status: true,
  });

  useEffect(() => {
    if (model) {
      setFormData({
        id: model.id,
        identifier: model.identifier || "",
        description: model.description || "",
        status: model.status ?? true,
      });
    }
  }, [model]);

  const fields = [
    {
      key: "identifier",
      label: "Model Name",
      type: "text",
      disabled: true,
    },
    {
      key: "brand",
      label: "Brand",
      type: "search",
      api: "/api/brand/list",
    },
    {
      key: "product",
      label: "Product",
      type: "search",
      api: "/api/product/list",
    },
    {
      key: "description",
      label: "Description",
      type: "text",
    },
  ];

  return (
    <CommonEdit
      title="Edit Model"
      icon={FiBox}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="model"
      onSubmit={() => onClose()}
      submitLabel="Update Model"
    />
  );
};
ModelProductEdit.propTypes = {
  model: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    identifier: PropTypes.string,
    description: PropTypes.string,
    status: PropTypes.bool,
  }),
  onClose: PropTypes.func,
};
export default ModelProductEdit;