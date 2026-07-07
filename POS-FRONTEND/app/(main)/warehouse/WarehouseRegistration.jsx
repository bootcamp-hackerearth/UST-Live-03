"use client";

import { useState } from "react";
import { FiHome } from "react-icons/fi";
import CommonAdd from "@/component/CommonAdd";

const WarehouseRegistration = ({ onClose }) => {
  const [formData, setFormData] = useState({
    identifier: "",
    address: "",
    country: "",
    pincode: "",
    shelves: [],
    status: true,
  });

  const fields = [
    {
      key: "identifier",
      label: "Warehouse Name",
      type: "text",
      required: true,
    },
    {
      key: "address",
      label: "Address",
      type: "text",
      required: true,
    },
    {
      key: "country",
      label: "Country",
      type: "text",
      required: true,
    },
    {
      key: "shelves",
      label: "Shelves",
      type: "search",
      api: "/api/shelf/list",
      required: true,
    },
    {
      key: "pincode",
      label: "Pincode",
      type: "number",
      required: true,
    },
  ];

  return (
    <CommonAdd
      title="Add Warehouse"
      icon={FiHome}
      formData={formData}
      setFormData={setFormData}
      fields={fields}
      moduleName="warehouse"
      onSubmit={() => {
        onClose?.();
      }}
      submitLabel="Add Warehouse"
    />
  );
};

export default WarehouseRegistration;
