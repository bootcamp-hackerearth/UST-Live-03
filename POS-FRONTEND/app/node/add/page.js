import React from "react";
import AddFormSkeleton from "@/app/components/AddFormSkeleton";

export default function AddNode() {
  const fields = [
    {
      name: "path",
      label: "Path",
      type: "text",
      required: true,
    },
    {
      name: "roles",
      label: "Role",
      type: "select",          
      api: "role",    
      optionLabel: "identifier",         
      optionValue: "identifier",
      multiple: true,          
    }
  ];

  return (
    <AddFormSkeleton
      title="Node"
      apiPath="node"
      fields={fields}
    />
  );
}