"use client";
import ListTemplate from "../components/ListTemplate";
import { auditColumns } from "../components/auditColumns";

export default function ShelfList() {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Identifier", field: "identifier" },
    { label: "Status", field: "status" },
    ...auditColumns(),
  ];

  return (
    <ListTemplate
      title="Shelf Management"
      columns={columns}
      urlName="shelf"
      showStatus={true}
      editKey="identifier"
      deleteKey="identifier"
      deleteParam="identifier"
      statusKey="identifier"
      addButtonLabel="Shelf"
      pageSize={10}
    />
  );
}