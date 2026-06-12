"use client";

import EditFormSkeleton from "../../../components/EditFormSkeleton"; 

export default function EditPrice() {
  const priceFields = [
    {
      key: "mrp",
      label: "MRP",
      type: "number",
    },
    {
      key: "sellingPrice",
      label: "Selling Price",
      type: "number",
    },
    {
      key: "costPrice",
      label: "Cost Price",
      type: "number",
    },
    {
      key: "effectiveFrom",
      label: "Effective From",
      type: "date",
    },
  ];

  return (
    <EditFormSkeleton
      title="Price"
      apiPath="price"
      extraFields={priceFields}
    />
  );
}