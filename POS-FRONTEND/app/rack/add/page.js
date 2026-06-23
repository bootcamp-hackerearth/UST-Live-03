"use client";
import React from "react";
import AddFormSkeleton from "@/app/components/AddFormSkeleton";

export default function AddRack() {
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
    <AddFormSkeleton
      title="Rack"
      apiPath="rack"
      fields={fields}
    />
  );
}