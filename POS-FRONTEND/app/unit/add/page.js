import React from "react";
import AddFormSkeleton from "@/app/components/AddFormSkeleton";

export default function AddUnit() {
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
      title="Unit"
      apiPath="unit"
      fields={fields}
    />
  );
}
