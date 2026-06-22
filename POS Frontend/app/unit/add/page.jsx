"use client";

import AddFormSkeleton from "@/components/CommonAddForm";

export default function AddUnit() {
  // If UnitDto has extra fields beyond identifier, add them here
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