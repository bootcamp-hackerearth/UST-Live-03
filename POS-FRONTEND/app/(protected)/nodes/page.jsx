"use client";

import CommonList from "@/components/table/CommonList";

export default function Nodes() {

  const columns = [
    {
      header: "Node Name",
      field: "identifier",
    },
    {
      header: "Path",
      field: "path",
    },
    {
      header: "Roles",
      field: "roles",
    },
  ];

  return (
    <CommonList
      title="Nodes"
      subtitle="Manage application navigation nodes"
      entity="node"
      addPath="/nodes/add"
      editPath="/nodes/edit"
      columns={columns}
    />
  );

}