"use client";
import CommonAddPage from "@/app/components/CommonAddPage";


export default function AddRackPage() {
  const fields = [
    { label: "Rack Name", name: "identifier", type: "text" },
    { 
      label: "Select Shelves", 
      name: "shelfs", 
      type: "dropdown",
      api: "/api/shelf/findallactive", 
      multiple: true,
      optionLabel: "identifier",
      optionValue: "identifier",
      placeholder: "Select shelves"
    },
    { 
      label: "Status", name: "status", type: "radio", 
      options: [{ label: "Active", value: true }, { label: "Inactive", value: false }] 
    }
  ];

  return (
    <CommonAddPage
      title="Add New Rack"
      submitApi="/api/racks/add"
      redirectRoute="/racks/list"
      fields={fields}
      initialValues={{ status: true, shelfs: [] }} 
    />
  );
}