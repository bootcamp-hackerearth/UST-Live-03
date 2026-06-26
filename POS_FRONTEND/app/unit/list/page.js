"use client";

import CommonList from "@/app/components/CommonList";

export default function UnitListPage() {
  const columns = [
    { header: "ID", field: "id" },
    { header: "Unit Name", field: "identifier" },
    { header: "Status", field: "status" }
  ];

  return (
    <CommonList
      title="Unit Management"
      apiUrl="/api/unit/list"
      addRoute="/unit/add"
      editRoute="/unit/edit/:identifier"
      deleteApi="/api/unit/delete"
      deleteParam="identifier"
      showStatus={true}
      toggleApi="/api/unit/toggle-status"
      toggleParam="identifier"
      toggleField="status"
      columns={columns}
    />
  );
}