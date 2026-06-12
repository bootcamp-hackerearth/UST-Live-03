"use client";
import { useParams } from "next/navigation";
import Update from "../../../../components/edit";

export default function EditPrice() {
  const params = useParams();

  const extraFields = [
    {
      key: "costPrice",
      label: "Cost Price",
      type: "number",
      required: true,
    },
    {
      key: "mrp",
      label: "MRP",
      type: "number",
      required: true,
    },
    {
      key: "sellingPrice",
      label: "Selling Price",
      type: "number",
      required: true,
    },
  ];

  return (
    <Update
      identifier={params.identifier}
      apiPath="price"
      title="Price"
      showDescription={false}
      extraFields={extraFields}
    />
  );
}
