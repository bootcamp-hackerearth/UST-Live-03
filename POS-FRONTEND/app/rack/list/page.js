"use client";

import CommonList from "@/app/components/CommonList";

export default function RackPage() {
  return (
    <CommonList
      title="Rack Management"
      apiUrl="/api/rack/list"
      method="POST"

      deleteApi="/api/rack/delete"
      deleteParam="identifier"

      editRoute="/rack/edit/:identifier"
      addRoute="/rack/add"

      showStatus={true}
      toggleApi="/api/rack/toggle-status"
      toggleParam="identifier"
      toggleField="status"
      toggleMethod="PATCH"

      columns={[
        {
          header: "ID",
          field: "id",
        },
        {
          header: "Rack Name",
          field: "identifier",
        },
        {
          header: "Shelves",
          field: "shelfs",
        },
        {
          header: "Status",
          field: "status",
        },
      ]}
    />
  );
}