"use client";

import { useState } from "react";
import { FiTag } from "react-icons/fi";
import CommonAdd from "@/component/CommonAdd";
import PropTypes from "prop-types";

const CategoryRegistration = ({ onClose }) => {
  const [formData, setFormData] = useState({
    identifier: "",
    superCategory: "",
  });

  const fields = [
    {
      key: "identifier",
      label: "Category Name",
      type: "text",
      required: true,
    },
    {
      key: "superCategory",
      label: "Super Category",
      type: "search",
      api: "/api/category/list",
    },
  ];

  return (
    <CommonAdd
      title="Add Category"
      icon={FiTag}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="category"
      onSubmit={() => {
        onClose();
      }}
      submitLabel="Add Category"
    />
  );
};
CategoryRegistration.propTypes = {
  onClose: PropTypes.func.isRequired,
};

export default CategoryRegistration;