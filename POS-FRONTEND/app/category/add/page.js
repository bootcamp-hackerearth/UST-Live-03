import React from "react";
import AddFormSkeleton from "@/app/components/AddFormSkeleton";

export default function AddCategory() {
  const fields = [
    {
      name: "superCategory",
      label: "Super Category",
      type: "select",
      api: "category", 
      optionLabel: "identifier",
      optionValue: "identifier",
      required: false,
    },
  ];

  return (
    <AddFormSkeleton
      title="Category"
      apiPath="category" 
      fields={fields}
    />
  );
}