"use client";

import { useState } from "react";
import EditFormSkeleton from "@/components/CommonEditForm";

export default function EditWareHouse() {
  const [location, setLocation] = useState("");
  const [manager, setManager] = useState("");

  const extraFields = [
    { key: "location", label: "Location", type: "text" },
    { key: "manager", label: "Manager", type: "text" },
  ];

  return (
    <EditFormSkeleton
      title="Warehouse"
      apiPath="wareHouse"
      extraFields={extraFields}
      setters={{ 
        location: setLocation, 
        manager: setManager 
      }}
    />
  );
}