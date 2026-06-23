import React from "react";
import EditFormSkeleton from "@/app/components/EditFormSkeleton";

export default function EditShelf() {
  const fields = [
    {
      name: "description",
      label: "Description",
      type: "text",
      required: false,
      placeholder: "Enter brand description",
    },
  ];

  return (
    <EditFormSkeleton
      title="Shelf"
      apiPath="shelf"
      paramKey="identifier"
      getParamKey="identifier"
      fields={fields}
    />
  );
}
