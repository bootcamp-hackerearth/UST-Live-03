"use client";

import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { FiArchive } from "react-icons/fi";
import CommonEdit from "@/component/CommonEdit";

const ShelfEdit = ({ shelf, onClose }) => {
  const [formData, setFormData] = useState({});

  useEffect(() => {
    setFormData(shelf || {});
  }, [shelf]);

  const fields = [
    {
      key: "identifier",
      label: "Shelf Name",
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
      label: "Active",
      type: "checkbox",
    },
  ];

  return (
    <CommonEdit
      title="Edit Shelf"
      icon={FiArchive}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="shelf"
      onSubmit={() => {
        onClose?.();
      }}
      submitLabel="Update Shelf"
    />
  );
};
ShelfEdit.propTypes = {
  onClose: PropTypes.func,
  shelf: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    identifier: PropTypes.string,
    description: PropTypes.string,
    status: PropTypes.bool,
  }),
};
export default ShelfEdit;