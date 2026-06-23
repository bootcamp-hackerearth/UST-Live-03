import React from "react";
import AddFormSkeleton from "@/app/components/AddFormSkeleton";

export default function AddBrand() {
  const fields = [
    {
      name: "description",
      label: "Description",
      type: "text",
      required: false,
      placeholder: "Enter brand description or notes...",
    },
  ];

  return (
    <AddFormSkeleton
      title="Brand"
      apiPath="brand"
      fields={fields}
    />
  );
}
