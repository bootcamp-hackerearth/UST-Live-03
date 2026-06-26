"use client";
import ListTemplate from "../components/ListTemplate";
import { auditColumns } from "../components/auditColumns";

export default function RacksList() {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Identifier", field: "identifier" },
    { label: "Shelves", field: "shelf", render: (item) => Array.isArray(item.shelf) && item.shelf.length ? item.shelf.join(", ") : "-" },
    { label: "Status", field: "status" },
    ...auditColumns(),
  ];

  return (
    <ListTemplate
      title="Racks Management"
      columns={columns}
      urlName="racks"
      showStatus={true}
      editKey="id"
      deleteKey="identifier"
      deleteParam="identifier"
      statusKey="identifier"
      addButtonLabel="Rack"
      pageSize={10}
    />
  );
}