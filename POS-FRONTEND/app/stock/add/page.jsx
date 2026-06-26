"use client";

import Add from "@/app/components/CommonAdd";

export default function Page() {

  const fields = [
    
    {
      name: "product",
      label: "Product",
      type: "singleDropdown",
      api: "/product/list-active",
      required: true,
    },

    {
      name: "warehouse",
      label: "Warehouse",
      type: "singleDropdown",
      api: "/warehouse/list-active",
      required: true,
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
    <Add
      urlName="stock"
      fields={fields}
    />
  );
}