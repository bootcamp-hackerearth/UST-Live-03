"use client";

import Edit from "@/app/components/CommonEdit";

export default function Page() {
  const fields = [
    {
      name: "identifier",
      label: "Name",
      type: "text", 
    },
    {
      name: "product",
      label: "Product",
      type: "singleDropdown",
      api: "/product/list-active",
      readOnly: true,
    },
    {
      name: "priceType",
      label: "Price Type",
      type: "select",
      options: [
        { label: "-- Select Price Type --", value: "" },
        { label: "Mrp", value: "Mrp" },
        { label: "sellingPrice", value: "sellingPrice" },
        { label: "costPrice", value: "costPrice" },
      ],
      readOnly: true,
    },
    {
      name: "sumPrice",
      label: "Total Price",
      type: "number",
    },
  ];
  return (
    <Edit
      urlName="price"
      fields={fields}
      identifier="identifier" 
    />
  );
}