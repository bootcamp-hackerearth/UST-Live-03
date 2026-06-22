"use client";

import { useState } from "react";
import AddFormSkeleton from "@/components/CommonAddForm";

export default function AddWareHouse() {
  const [location, setLocation] = useState("");
  const [manager, setManager] = useState("");

  const extraFields = [
    { key: "location", label: "Location", type: "text" },
    { key: "manager", label: "Manager", type: "text" },
  ];

  return (
    <AddFormSkeleton
      title="Warehouse"
      apiPath="wareHouse"
      extraFields={extraFields}
      extraData={{ location, manager }}
    />
  );
}