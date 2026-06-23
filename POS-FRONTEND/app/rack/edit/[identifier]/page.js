"use client";
import React from "react";
import EditFormSkeleton from "@/app/components/EditFormSkeleton";

export default function EditRack() {
  const fields = [
    {
      name: "shelfs",
      label: "Shelfs",
      type: "select",
      multiple: true,
      api: "shelf",
      optionLabel: "identifier",
      optionValue: "identifier",
    },
  ];

  return (
    <EditFormSkeleton
      title="Rack"
      apiPath="rack"
      paramKey="identifier"
      getParamKey="identifier"
      fields={fields}
    />
  );
}