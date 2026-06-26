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
    status: true,
  });

  const fields = [
    {
      key: "identifier",
      label: "Warehouse Name",
      type: "text",
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
