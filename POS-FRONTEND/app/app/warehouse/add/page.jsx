"use client";

import Add from "../../../components/add";

export default function WarehouseAdd() {
  const extraFields = [
    {
      key: "country",
      label: "Country",
      required: true,
    },
    {
      key: "pincode",
      label: "Pincode",
      required: true,
    },
    {
      key: "address",
      label: "Address",
      required: true,
    },
  ];

  return (
    <Add
      title="Warehouse"
      apiPath="warehouse"
      extraFields={extraFields}
      showDescription={false}
    />
  );
}
