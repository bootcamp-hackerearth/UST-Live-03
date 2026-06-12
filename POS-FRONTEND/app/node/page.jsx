"use client";

import ListTemplate from "../components/ListTemplate";

export default function NodeList() {
  const columns = [
    {
      label: "Identifier",
      field: "identifier",
    },
    {
      label: "Path",
      field: "path",
    },
    {
      label: "Roles",
      field: "roles",
      render: (item) => (Array.isArray(item.roles) ? item.roles.join(", ") : "-"),
    },
    {
      label: "Status",
      field: "status",
    },
  ];

  return (
    <ListTemplate
      title="Node Management"
      columns={columns}
      urlName="node"
      showStatus={true}
      editKey="identifier"
      deleteKey="identifier"
      deleteParam="identifier"
      rowKey="identifier"
      addButtonLabel="Node"
      pageSize={10}
      sortField="identifier"
      editUseQuery={true}
    />
  );
}
