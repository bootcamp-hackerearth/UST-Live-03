"use client";

import CommonList from "../../components/CommonList";

export default function BrandListPage() {
  const columns = [
    { header: "ID", field: "id" },
    { header: "Identifier", field: "identifier" },
    { header: "description", field: "description" },
    { header: "Status", field: "status" }
  ];

  return (
    <CommonList
      title="Brand Management"
      apiUrl="/api/brand/list"
      addRoute="/brand/add"
      editRoute="/brand/edit/:identifier"
      deleteApi="/api/brand/delete"
      deleteParam="identifier"
      showStatus={true}
      toggleApi="/api/brand/toggle-status"
      toggleParam="identifier"
      toggleField="status"
      columns={columns}
    />
  );
}