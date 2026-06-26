"use client";
import ListTemplate from "../components/ListTemplate";
import { auditColumns } from "../components/auditColumns";

export default function WarehouseList() {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Identifier", field: "identifier" },
    { label: "Code", field: "code" },
    { label: "Location", field: "location" },
    { label: "Address", field: "address" },
    { label: "Status", field: "status" },
    ...auditColumns(),
  ];

  return (
    <ListTemplate
      title="Warehouse Management"
      columns={columns}
      urlName="warehouse"
      showStatus={true}
      editKey="id"
      deleteKey="identifier"
      deleteParam="identifier"
      statusKey="identifier"
      addButtonLabel="Warehouse"
      pageSize={10}
    />
  );
}