"use client";

import CommonAddPage from "@/app/components/CommonAddPage";

export default function AddShelfPage() {
  const fields = [
    { label: "Shelf Name", name: "identifier", type: "text" },
    { 
      label: "Status", 
      name: "status", 
      type: "radio", 
      options: [
        { label: "Active", value: "true" },
        { label: "Inactive", value: "false" }
      ] 
    }
  ];

  return (
    <CommonAddPage
      title="Add New Shelf"
      submitApi="/api/shelf/add"
      redirectRoute="/shelf/list"
      fields={fields}
      initialValues={{ identifier: "", status: "true" }}
    />
  );
}