import React from "react";
import EditFormSkeleton from "@/app/components/EditFormSkeleton";

export default function EditModel() {
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
      title="Model"
      apiPath="model"
      paramKey="identifier"
      getParamKey="identifier"
      fields={fields}
    />
  );
}
