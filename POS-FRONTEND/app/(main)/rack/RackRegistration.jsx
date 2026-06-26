"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import { FiArchive } from "react-icons/fi";
import CommonAdd from "@/component/CommonAdd";

const RackRegistration = ({ onClose, refreshData }) => {
  const [formData, setFormData] = useState({
    identifier: "",
    shelfs: "",
    description: "",
    status: true,
  });

  const fields = [
    {
      key: "identifier",
      label: "Rack Name",
      type: "text",
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
    <CommonAdd
      title="Rack"
      icon={FiArchive}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="racks"
      submitLabel="Save Rack"
      onSubmit={() => {
        refreshData?.();
        onClose?.();
      }}
    />
  );
};
RackRegistration.propTypes = {
  onClose: PropTypes.func,
  refreshData: PropTypes.func,
}
export default RackRegistration;
