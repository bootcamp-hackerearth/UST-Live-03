"use client";

import CommonList from "@/app/components/CommonList";

export default function WarehouseListPage() {
  const columns = [
    { header: "ID", field: "id" },
    { header: "Name", field: "identifier" },
    { header: "Location", field: "location" },
    { header: "Capacity", field: "capacity" },
    { header: "Status", field: "status" }
  ];

  return (
    <CommonList
      title="Warehouse Management"
      apiUrl="/api/warehouse/list"
      addRoute="/warehouse/add"
      editRoute="/warehouse/edit/:identifier"
      deleteApi="/api/warehouse/delete"
      deleteParam="identifier"
      showStatus={true}
      toggleApi="/api/warehouse/toggle-status"
      toggleParam="identifier"
      toggleField="status"
      columns={columns}
    />
  );
}