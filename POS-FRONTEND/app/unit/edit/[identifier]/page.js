import React from "react";
import EditFormSkeleton from "@/app/components/EditFormSkeleton";

export default function EditUnit() {
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
      title="Unit"
      apiPath="unit"
      paramKey="identifier"
      getParamKey="identifier"
      fields={fields}
    />
  );
}
