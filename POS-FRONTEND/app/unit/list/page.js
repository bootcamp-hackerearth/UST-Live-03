"use client";

import CommonList from "@/app/components/CommonList";

export default function UnitPage() {
  return (
    <CommonList
      title="Unit Management"
      apiUrl="/api/unit/list"
      method="POST"

      deleteApi="/api/unit/delete"
      deleteParam="identifier"

      editRoute="/unit/edit/:identifier"

      addRoute="/unit/add"

      showStatus={true}
      toggleApi="/api/unit/toggle-status"
      toggleParam="identifier"
      toggleField="status"
      toggleMethod="PATCH"

      columns={[
        { header: "Sl No", field: "id" },
        { header: "Unit Name", field: "identifier" },
        { header: "Description", field: "description" }, 
        { header: "Status", field: "status" },
      ]}
    />
  );
}