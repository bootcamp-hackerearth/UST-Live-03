"use client";

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { FiArchive } from "react-icons/fi";
import CommonEdit from "@/component/CommonEdit";

const RackEdit = ({ rack, onClose, refreshData }) => {
  const [formData, setFormData] = useState(rack || {});

  useEffect(() => {
    setFormData(rack || {});
  }, [rack]);

  const fields = [
    {
      key: "identifier",
      label: "Rack Name",
      type: "text",
      disabled: true,
    },
    {
      key: "shelfs",
      label: "Shelf",
      type: "search",
      api: "/api/shelf/list",
    },
    {
      key: "description",
      label: "Description",
      type: "text",
    },
  ];

  return (
    <CommonEdit
      title="Rack"
      icon={FiArchive}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="racks"
      submitLabel="Update Rack"
      onSubmit={() => {
        refreshData?.();
        onClose?.();
      }}
    />
  );
};
RackEdit.propTypes = {
  onClose: PropTypes.func,
  rack: PropTypes.func,
  refreshData: PropTypes.func,
}
export default RackEdit;
