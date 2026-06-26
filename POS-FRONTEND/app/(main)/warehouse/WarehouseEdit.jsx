"use client";

import { useState, useEffect } from "react";
import { FiHome } from "react-icons/fi";
import CommonEdit from "@/component/CommonEdit";

const WarehouseEdit = ({ warehouse, onClose, refreshData }) => {
  const [formData, setFormData] = useState(warehouse || {});

  useEffect(() => {
    setFormData(warehouse || {});
  }, [warehouse]);

  const fields = [
    {
      key: "identifier",
      label: "Warehouse Name",
      type: "text",
      disabled: true,
    },
    {
      key: "address",
      label: "Address",
      type: "text",
    },
    {
      key: "country",
      label: "Country",
      type: "text",
    },
    {
      key: "pincode",
      label: "Pincode",
      type: "number",
    },
    {
      key: "status",
      label: "Status",
      type: "checkbox",
    },
  ];

  return (
    <CommonEdit
      title="Edit Warehouse"
      icon={FiHome}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="warehouse"
      submitLabel="Update Warehouse"
      onSubmit={() => {
        refreshData?.();
        onClose?.();
      }}
    />
  );
};

export default WarehouseEdit;
