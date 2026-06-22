"use client";

import { useState } from "react";
import EditFormSkeleton from "@/components/CommonEditForm";

export default function EditBrand() {
  const [description, setDescription] = useState("");

  const extraFields = [
    { key: "description", label: "Description", type: "text" },
  ];

  return (
    <EditFormSkeleton
      title="Brand"
      apiPath="brand"
      paramName="identifier"
      identifierField="identifier"
      extraFields={extraFields}
      setters={{ description: setDescription }}
    />
  );
}