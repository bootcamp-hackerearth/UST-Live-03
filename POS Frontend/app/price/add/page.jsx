"use client";

import  CommonAddForm from "@/components/CommonAddForm"; // adjust path as needed

const extraFields = [
  { key: "mrp",           label: "MRP",           type: "text" },
  { key: "sellingPrice",  label: "Selling Price",  type: "text" },
  { key: "costPrice",     label: "Cost Price",     type: "text" },
  { key: "effectiveFrom", label: "Effective From", type: "date" },
];

export default function AddPrice() {
  return (
    <CommonAddForm
      title="Price"
      apiPath="price"
      apiEndpoint="add"
      extraFields={extraFields}
      successKeys={["id", "mrp"]}   
    />
  );
}