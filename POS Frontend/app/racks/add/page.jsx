"use client";

import { useState } from "react";
import AddFormSkeleton from "@/components/CommonAddForm";

export default function AddRacks() {
  const [shelfs, setShelfs] = useState([]);

  // Replace this static array with fetched data if needed
  const shelfOptions = [
    { value: "shelf1", label: "Shelf 1" },
    { value: "shelf2", label: "Shelf 2" },
  ];

  const extraFields = [
    { key: "shelfs", label: "Shelfs", type: "multiselect", options: shelfOptions },
  ];

  return (
    <AddFormSkeleton
      title="Racks"
      apiPath="racks"
      extraFields={extraFields}
      extraData={{ shelfs }}
    />
  );
}