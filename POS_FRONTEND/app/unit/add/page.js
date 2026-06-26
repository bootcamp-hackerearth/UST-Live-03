"use client";

import CommonAddPage from "@/app/components/CommonAddPage";

export default function AddUnitPage() {
  const fields = [
    { label: "Unit Name", name: "identifier", type: "text" },
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
      title="Add New Unit"
      submitApi="/api/unit/add"
      redirectRoute="/unit/list"
      fields={fields}
      initialValues={{ identifier: "", status: "true" }}
    />
  );
}