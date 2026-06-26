"use client";

import ListTemplate from "../components/ListTemplate";
import { auditColumns } from "../components/auditColumns";

export default function NodeList() {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Identifier", field: "identifier" },
    { label: "Path", field: "path" },
    { label: "Status", field: "status" },
    ...auditColumns(),
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
      statusKey="identifier"
      addButtonLabel="Node"
      pageSize={10}
    />
  );
}