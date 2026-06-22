"use client";

import EditFormSkeleton from "@/components/CommonEditForm";

export default function EditModels() {
  // Empty this array to remove all additional fields
  const extraFields = []; 

  return (
    <EditFormSkeleton
      title="Models"
      apiPath="models"
      extraFields={extraFields}
      extraData={{}} // Pass empty objects as there are no extra fields
      setters={{}}
    />
  );
}