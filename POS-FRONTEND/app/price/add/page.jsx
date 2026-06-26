"use client";

import Add from "@/app/components/CommonAdd";

export default function Page() {
  const fields = [
    
    {
      name: "product",  
      label: "Product",
      type: "singleDropdown",
      api: "/product/list-active",
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
    },
    {
      name: "sumPrice",
      label: "Total Price",
      type: "number", 
    },
  ];
  return <Add urlName="price" fields={fields} />;
}