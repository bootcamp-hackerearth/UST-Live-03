// app/pos/roles/page.jsx

"use client";

import BaseListForm from "../../../components/lists/BaseListForm";

export default function RoleListPage() {
  const columns = [
    {
      key: "identifier",
      label: "Name",
    },
    {
      key: "description",
      label: "Description",
    },
    {
      key: "status",
      label: "Status",
    },
  ];

  return (
    <BaseListForm
      title="Roles"
      entity="role"
      columns={columns}
      addPath="/pos/roles/add"
      editPath="/pos/roles/edit"
      identifierKey="identifier"
    />
  );
}