"use client";

import EditFormSkeleton from "@/components/CommonEditForm";

export default function EditWareHouse() {
  const extraFields = [
    { key: "location", label: "Location", type: "text" },
    { key: "manager", label: "Manager", type: "text" },
  ];

  return (
    <EditFormSkeleton
      title="Warehouse"
      apiPath="wareHouse"
      extraFields={extraFields}
    />
  );
}