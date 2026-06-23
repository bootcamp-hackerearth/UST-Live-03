import React from "react";
import AddFormSkeleton from "@/app/components/AddFormSkeleton";

export default function AddShelf() {
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
      title="Shelf"
      apiPath="shelf"
      fields={fields}
    />
  );
}