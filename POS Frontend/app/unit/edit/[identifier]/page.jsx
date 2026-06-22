"use client";

import EditFormSkeleton from "@/components/CommonEditForm";

export default function EditUnit() {
  const extraFields = []; // Include extra fields if necessary

  return (
    <EditFormSkeleton
      title="Unit"
      apiPath="unit"
      extraFields={extraFields}
      extraData={{}}
      setters={{}}
    />
  );
}