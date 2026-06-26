"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import { FiArchive } from "react-icons/fi";
import CommonAdd from "@/component/CommonAdd";

const ShelfRegistration = ({ onClose }) => {
  const [formData, setFormData] = useState({
    identifier: "",
    description: "",
    status: true,
  });

  const fields = [
    {
      key: "identifier",
      label: "Shelf Name",
      type: "text",
    },
    {
      key: "description",
      label: "Description",
      type: "text",
    },
  ];

  return (
    <CommonAdd
      title="Add Shelf"
      icon={FiArchive}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="shelf"
      onSubmit={() => {
        onClose?.();
      }}
      submitLabel="Add Shelf"
    />
  );
};
ShelfRegistration.propTypes = {
  onClose: PropTypes.func,
};
export default ShelfRegistration;
