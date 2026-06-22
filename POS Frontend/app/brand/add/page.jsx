"use client";

import { useState } from "react";
import AddFormSkeleton from "@/components/CommonAddForm";

export default function AddBrand() {
  const [description, setDescription] = useState("");

  const extraFields = [
    { key: "description", label: "Description", type: "text" },
  ];

  return (
    <AddFormSkeleton
      title="Brand"
      apiPath="brand"
      extraFields={extraFields}
    />
  );
}