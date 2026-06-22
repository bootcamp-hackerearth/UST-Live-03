"use client";

import EditFormSkeleton from "@/components/CommonEditForm";

export default function EditShelfs() {
  return (
    <EditFormSkeleton
      title="Shelfs"
      apiPath="shelfs"
      extraFields={[]} // No extra fields required
      extraData={{}}
      setters={{}}
    />
  );
}