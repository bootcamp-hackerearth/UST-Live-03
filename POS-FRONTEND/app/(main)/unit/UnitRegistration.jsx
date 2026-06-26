"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import { FiArchive } from "react-icons/fi";
import CommonAdd from "@/component/CommonAdd";

const UnitRegistration = ({ onClose }) => {
  const [formData, setFormData] = useState({
    identifier: "",
    description: "",
    status: true,
  });

  const fields = [
    {
      key: "identifier",
      label: "Unit Name",
      type: "text",
    },
    {
      key: "description",
      label: "Description",
      type: "textarea",
    },
    {
      key: "status",
      label: "Active",
      type: "checkbox",
    },
  ];

  return (
    <CommonAdd
      title="Add Unit"
      icon={FiArchive}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="unit"
      onSubmit={() => onClose()}
      submitLabel="Save Unit"
    />
  );
};

UnitRegistration.propTypes = {
  onClose: PropTypes.func.isRequired,
};

export default UnitRegistration;
