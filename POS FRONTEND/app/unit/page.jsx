"use client";
import ListTemplate from "../components/ListTemplate";
import { auditColumns } from "../components/auditColumns";

export default function UnitList() {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Identifier", field: "identifier" },
    ...auditColumns(),
  ];

  return (
    <ListTemplate
      title="Unit Management"
      columns={columns}
      urlName="unit"
      showStatus={false}
      editKey="id"
      deleteKey="identifier"
      deleteParam="identifier"
      addButtonLabel="Unit"
      pageSize={10}
    />
  );
}