"use client";

import CommonAddPage from "@/app/components/CommonAddPage";

export default function AddWarehousePage() {
  const fields = [
    { label: "Warehouse Name", name: "identifier", type: "text" },
    { label: "Location", name: "location", type: "text" },
    { label: "Capacity", name: "capacity", type: "number" },
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
      title="Add Warehouse"
      submitApi="/api/warehouse/add"
      redirectRoute="/warehouse/list"
      fields={fields}
      initialValues={{ identifier: "", location: "", capacity: "", status: "true" }}
    />
  );
}