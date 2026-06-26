"use client";

import AddFormSkeleton from "@/components/CommonAddForm";

export default function AddWareHouse() {
  const extraFields = [
    { key: "location", label: "Location", type: "text" },
    { key: "manager", label: "Manager", type: "text" },
  ];

  return (
    <AddFormSkeleton
      title="Warehouse"
      apiPath="wareHouse"
      extraFields={extraFields}
    />
  );
}