import React from "react";
import EditFormSkeleton from "@/app/components/EditFormSkeleton"; 

export default function EditWareHouse() {
  const fields = [
    {
      name: "location",
      label: "Location",
      type: "text",
      required: true,
    },
    {
      name: "manager",
      label: "Manager",
      type: "text",
      required: true,
    },
  ];

  return (
    <EditFormSkeleton
      title="WareHouse"
      apiPath="warehouse"
      fields={fields}
    />
  );
}