"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import { FiArchive } from "react-icons/fi";
import CommonAdd from "@/component/CommonAdd";

const ShelfRegistration = ({ onClose }) => {
  const [formData, setFormData] = useState({
    identifier: "",
    description: "",
    racks: [],
    status: true,
  });

  const fields = [
    {
      key: "identifier",
      label: "Shelf Name",
      type: "text",
      required: true,
    },
    {
      key: "racks",
      label: "Racks",
      type: "search",
      api: "/api/racks/list",
      required: true,
    },
    {
      key: "description",
      label: "Description",
      type: "text",
      required: true,
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
