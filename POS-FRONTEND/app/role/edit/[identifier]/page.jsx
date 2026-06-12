"use client";

import EditFormSkeleton from "@/components/EditSkeleton";

export default function EditRole() {
  return (
    <EditFormSkeleton
      title="Role"
      apiPath="role"
      paramName="identifier"
      identifierField="identifier"
      extraFields={[
        {
          key: "description",
          label: "Description",
          type: "text",
        },
      ]}
    />
  );
}