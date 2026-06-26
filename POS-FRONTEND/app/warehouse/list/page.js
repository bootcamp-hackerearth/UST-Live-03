"use client";

import CommonList from "../../components/CommonList";

export default function WarehousePage() {
  return (
    <CommonList
      title="Warehouse Management"

      apiUrl="/api/warehouse/list"
      method="POST"

      deleteApi="/api/warehouse/delete"
      deleteParam="identifier"

      editRoute="/warehouse/edit/:identifier"

      addRoute="/warehouse/add"

      showStatus={true}
      toggleApi="/api/warehouse/toggle-status"
      toggleParam="identifier"
      toggleField="status"
      toggleMethod="PATCH"

      columns={[
        { header: "ID", field: "id" },
        { header: "Identifier", field: "identifier" },
        { header: "Location", field: "location" },
        { header: "Capacity", field: "capacity" },
        { header: "Status", field: "status" },
      ]}
    />
  );
}