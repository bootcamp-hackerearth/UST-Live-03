"use client";

import AddFormSkeleton from "@/components/CommonAddForm";

export default function AddModels() {
  const extraFields = [
    { key: "name", label: "Model Name", type: "text" }
  ];

  return (
    <AddFormSkeleton
      title="Models"
      apiPath="models"
      extraFields={extraFields}
    />
  );
}