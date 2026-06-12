import React from "react";
import AddFormSkeleton from "@/app/components/AddFormSkeleton";

export default function AddRole() {
  const fields = [
   
    
    {
      name: "description",
      label: "Description",
      type: "text",
      required: false,
      placeholder: "Enter brief brand description or notes...",
    },
  ];

  return (
    <AddFormSkeleton
      title="Role"
      apiPath="role"
      fields={fields}
    />
  );
}