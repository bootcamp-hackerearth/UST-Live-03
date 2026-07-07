"use client";

import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { FiArchive } from "react-icons/fi";
import CommonEdit from "@/component/CommonEdit";

const ShelfEdit = ({ shelf, onClose }) => {
  const [formData, setFormData] = useState({
    identifier: "",
    description: "",
    racks: [],
    status: true,
  });

  useEffect(() => {
    setFormData({
      identifier: shelf?.identifier || "",
      description: shelf?.description || "",
      racks: shelf?.racks || [],
      status: shelf?.status ?? true,
    });
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
      key: "racks",
      label: "Racks",
      type: "search",
      api: "/api/racks/list",
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
    racks: PropTypes.arrayOf(PropTypes.string),
    description: PropTypes.string,
    status: PropTypes.bool,
  }),
};
export default ShelfEdit;