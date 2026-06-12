import React from "react";
import EditFormSkeleton from "@/app/components/EditFormSkeleton";

export default function EditRole() {
  const fields = [

    {
      name: "description",
      label: "Description",
      type: "text",
      required: false,
      placeholder: "Enter role description",
    },
  ];

  return (
    <EditFormSkeleton
      title="Role"
      apiPath="role"
      paramKey="identifier"     
      getParamKey="identifier"  
      fields={fields}
    />
  );
}
