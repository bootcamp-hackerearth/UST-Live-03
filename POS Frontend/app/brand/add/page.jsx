"use client";

import AddFormSkeleton from "@/components/CommonAddForm";

export default function AddBrand() {
  const extraFields = [
    { key: "description", label: "Description", type: "text" },
  ];

  return (
    <AddFormSkeleton
      title="Brand"
      apiPath="brand"
      extraFields={extraFields}
    />
  );
}