"use client";

import AddForm from "@/app/components/CommonAddPage";

export default function AddRolePage() {
  const fields = [
    {
      name: "identifier",
      label: "Role Identifier",
      type: "text",
    },
    {
      name: "description",
      label: "Description",
      type: "text", // ✅ use text (since textarea not supported yet)
    },
  ];

  return (
    <AddForm
      title="Add Role"
      submitApi="/api/role/add"   // ✅ use API directly
      redirectRoute="/role/list"      // ✅ where to go after save
      fields={fields}
      initialValues={{
        identifier: "",
        description: "",
      }}
      submitButtonText="Save Role"
    />
  );
}
