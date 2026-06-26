"use client";

import EditFormSkeleton from "@/components/CommonEditForm";

export default function EditUnit() {
  const extraFields = []; 

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