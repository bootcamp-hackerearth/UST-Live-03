"use client";

import Edit from "@/app/components/CommonEdit";

export default function Page() {

  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
      readOnly: true,
    },

    {
      name: "product",
      label: "Product",
      type: "singleDropdown",   
      api: "/product/list-active",
      required: true,
      readOnly: true,
    },

    {
      name: "warehouse",
      label: "Warehouse",
      type: "singleDropdown",
      api: "/warehouse/list-active",
      required: true,
      readOnly: true,
    },

    {
      name: "quantity",
      label: "Quantity",
      type: "number",
    },

    {
      name: "minimumstock",
      label: "Minimum Stock Level",
      type: "number",
    },
  ];

  return (
    <Edit
      urlName="stock"
      fields={fields}
      identifier="identifier"
    />
  );
}