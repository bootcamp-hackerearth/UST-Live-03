"use client";

import { useState } from "react";
import EditFormSkeleton from "@/components/CommonEditForm";

// 1. Ensure the function is defined clearly
export default function EditRacks() {
  const [shelfs, setShelfs] = useState([]);

  // Mock options - replace with your dynamic fetch if implemented
  const shelfOptions = [
    { value: "shelf1", label: "Shelf 1" },
    { value: "shelf2", label: "Shelf 2" },
  ];

  const extraFields = [
    { key: "shelfs", label: "Shelfs", type: "multiselect", options: shelfOptions },
  ];

  // 2. Ensure it returns JSX
  return (
    <EditFormSkeleton
      title="Racks"
      apiPath="racks"
      extraFields={extraFields}
      extraData={{ shelfs }}
      setters={{ shelfs: setShelfs }}
    />
  );
}