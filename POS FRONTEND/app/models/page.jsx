"use client";
import ListTemplate from "../components/ListTemplate";
import { auditColumns } from "../components/auditColumns";

export default function ModelsList() {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Identifier", field: "identifier" },
    { label: "Brand", field: "brand" },
    { label: "Status", field: "status" },
    ...auditColumns(),
  ];

  return (
    <ListTemplate
      title="Models Management"
      columns={columns}
      urlName="models"
      showStatus={true}
      editKey="id"
      deleteKey="identifier"
      deleteParam="identifier"
      statusKey="identifier"
      addButtonLabel="Model"
      pageSize={10}
    />
  );
}