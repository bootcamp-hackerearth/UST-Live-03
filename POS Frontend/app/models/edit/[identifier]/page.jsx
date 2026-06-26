"use client";

import EditFormSkeleton from "@/components/CommonEditForm";

export default function EditModels() {
  const extraFields = []; 

  return (
    <EditFormSkeleton
      title="Models"
      apiPath="models"
      extraFields={extraFields}
      extraData={{}} 
      setters={{}}
    />
  );
}