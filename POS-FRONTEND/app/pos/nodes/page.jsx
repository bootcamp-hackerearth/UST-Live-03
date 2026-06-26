// app/pos/nodes/page.jsx

"use client";

import BaseListForm from "../../../components/lists/BaseListForm";

export default function NodeListPage() {
  const columns = [
    { key: "identifier", label: "Node Name" },
    { key: "path", label: "Path" },
    {
      key: "roles",
      label: "Roles",
      render: (roles) => roles && Array.isArray(roles) ? roles.join(", ") : "-"
    },
    { key: "status", label: "Status" },
  ];

  return (
    <BaseListForm
      title="Nodes"
      entity="node"
      columns={columns}
      addPath="/pos/nodes/add"
      editPath="/pos/nodes/edit"
      identifierKey="identifier"
    />
  );
}