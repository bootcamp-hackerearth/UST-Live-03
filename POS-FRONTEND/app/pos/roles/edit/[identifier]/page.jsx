// app/pos/roles/edit/[identifier]/page.jsx

"use client";

import BaseEditForm from "../../../../../components/edit/BaseEditForm";

export default function EditRolePage() {
  const fields = [
    {
      key: "description",
      label: "Role Description",
      type: "text",
    },
  ];

  return (
    <BaseEditForm
      title="Role"
      apiPath="role"
      extraFields={fields}
    />
  );
}