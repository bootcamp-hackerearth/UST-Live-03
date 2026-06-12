// app/pos/roles/add/page.jsx

"use client";

import BaseAddForm from "../../../../components/add/BaseAddForm";

export default function AddRolePage() {
  const fields = [
    {
      key: "description",
      label: "Role Description",
      type: "text",
    },
  ];

  return (
    <BaseAddForm
      title="Role"
      apiPath="role"
      extraFields={fields}
    />
  );
}