"use client";

import CommonAddPage from "@/app/components/CommonAddPage";

export default function AddBrandPage() {
  const fields = [
   
    { label: "Identifier", name: "identifier", type: "text" },
    { label: "description", name: "description", type: "text" },
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
      title="Add New Brand"
      submitApi="/api/brand/add"
      redirectRoute="/brand/list"
      fields={fields}
     initialValues={{ identifier: "", status: "true" }}
    />
  );
}