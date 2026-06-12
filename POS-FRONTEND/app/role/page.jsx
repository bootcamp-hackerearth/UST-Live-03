"use client";

import ListTemplate from "../components/ListTemplate";

export default function RoleList() {
  const columns = [
    {
      label: "Identifier",
      field: "identifier",
    },
    {
      label: "Description",
      field: "description",
    },
    {
      label: "Status",
      field: "status",
    },
  ];

  return (
    <ListTemplate
      title="Role Management"
      columns={columns}
      urlName="role"
      showStatus={true}
      editKey="identifier"
      deleteKey="identifier"
      deleteParam="identifier"
      rowKey="identifier"
      addButtonLabel="Role"
      pageSize={10}
      sortField="identifier"
      editUseQuery={true}
    />
  );
}