import React from "react";
import EditFormSkeleton from "@/app/components/EditFormSkeleton";

export default function EditCategory() {
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
    <EditFormSkeleton
      title="Category"
      apiPath="category"
      paramKey="identifier"
      getParamKey="identifier"
      fields={fields}
    />
  );
}