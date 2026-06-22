"use client";

import { useState } from "react";
import AddFormSkeleton from "@/components/CommonAddForm";

export default function AddModels() {
  // Define fields locally
  const extraFields = [
    { key: "name", label: "Model Name", type: "text" }
    // Add other fields here as needed
  ];

  // Local state for the fields
  const [name, setName] = useState("");

  return (
    <AddFormSkeleton
      title="Models"
      apiPath="models"
      extraFields={extraFields}
      extraData={{ name }} 
    />
  );
}