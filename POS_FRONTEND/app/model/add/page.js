"use client";

import CommonAddPage from "@/app/components/CommonAddPage";

export default function AddModelPage() {
  const fields = [
    { label: "Model Name", name: "identifier", type: "text" },
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
      title="Add New Model"
      submitApi="/api/model/add"
      redirectRoute="/model/list"
      fields={fields}
     initialValues={{ identifier: "", status: "true" }}
    />
  );
}