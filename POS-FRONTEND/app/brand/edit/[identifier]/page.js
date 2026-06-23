import React from "react";
import EditFormSkeleton from "@/app/components/EditFormSkeleton";

export default function EditBrand() {
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
      title="Brand"
      apiPath="brand"
      paramKey="identifier"
      getParamKey="identifier"
      fields={fields}
    />
  );
}
