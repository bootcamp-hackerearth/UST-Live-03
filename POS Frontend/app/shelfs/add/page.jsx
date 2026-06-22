"use client";

import AddFormSkeleton from "@/components/CommonAddForm";

export default function AddShelfs() {
  return (
    <AddFormSkeleton
      title="Shelfs"
      apiPath="shelfs"
      extraFields={[]} // No extra fields required
      extraData={{}}
    />
  );
}