"use client";

import { useState, useEffect } from "react";
import { FiEdit } from "react-icons/fi";
import CommonEdit from "@/component/CommonEdit";
import PropTypes from "prop-types";

const CategoryEdit = ({ category, onClose }) => {
  const [formData, setFormData] = useState({
    identifier: "",
    superCategory: "",
    description: "",
  });

  useEffect(() => {
    if (category) {
      setFormData({
        identifier: category.identifier || "",
        superCategory: category.superCategory || "",
        description: category.description || "",
      });
    }
  }, [category]);

  const fields = [
    {
      key: "identifier",
      label: "Category Name",
      type: "text",
      disabled: true,
    },
    {
      key: "superCategory",
      label: "Super Category",
      type: "search",
      api: "/api/category/list",
      excludeCurrent: true,
    },
  ];

  return (
    <CommonEdit
      title="Edit Category"
      icon={FiEdit}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="category"
      onSubmit={() => {
        onClose();
      }}
      submitLabel="Update Category"
    />
  );
};
CategoryEdit.propTypes = {
  category: PropTypes.shape({
    identifier: PropTypes.string,
    superCategory: PropTypes.string,
    description: PropTypes.string,
  }),
  onClose: PropTypes.func.isRequired,
};

export default CategoryEdit;