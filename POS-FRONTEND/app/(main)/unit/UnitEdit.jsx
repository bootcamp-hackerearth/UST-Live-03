"use client";

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { FiArchive } from "react-icons/fi";
import CommonEdit from "@/component/CommonEdit";

const UnitEdit = ({ unit, onClose }) => {
  const [formData, setFormData] = useState({});

  useEffect(() => {
    setFormData(unit || {});
  }, [unit]);

  const fields = [
    {
      key: "identifier",
      label: "Unit Name",
      type: "text",
      disabled: true,
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
    <CommonEdit
      title="Edit Unit"
      icon={FiArchive}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="unit"
      onSubmit={() => onClose()}
      submitLabel="Update Unit"
    />
  );
};
UnitEdit.propTypes = {
  unit: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    identifier: PropTypes.string,
    description: PropTypes.string,
    status: PropTypes.bool,
  }),
  onClose: PropTypes.func.isRequired,
};

export default UnitEdit;
