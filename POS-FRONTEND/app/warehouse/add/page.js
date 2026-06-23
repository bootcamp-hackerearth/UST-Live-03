import React from "react";
import AddFormSkeleton from "@/app/components/AddFormSkeleton"; 

export default function AddWareHouse() {
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
    <AddFormSkeleton
      title="WareHouse"
      apiPath="warehouse"
      fields={fields}
    />
  );
}