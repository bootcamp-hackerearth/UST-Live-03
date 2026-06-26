"use client";

import AddFormSkeleton from "@/components/CommonAddForm";

export default function AddUnit() {
  const extraFields = []; 

  return (
    <AddFormSkeleton
      title="Unit"
      apiPath="unit"
      extraFields={extraFields}
      extraData={{}}
    />
  );
}