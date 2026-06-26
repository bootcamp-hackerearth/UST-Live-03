"use client";

import CommonList from "../../components/CommonList";

export default function ShelfPage() {
  return (
    <CommonList
      title="Shelf Management"

      apiUrl="/api/shelf/list"
      method="POST"

      deleteApi="/api/shelf/delete"
      deleteParam="identifier"

      editRoute="/shelf/edit/:identifier"

      addRoute="/shelf/add"

      showStatus={true}
      toggleApi="/api/shelf/toggle-status"
      toggleParam="identifier"
      toggleField="status"
      toggleMethod="PATCH"

      columns={[
        { header: "ID", field: "id" },
        { header: "Shelf Name", field: "identifier" },
        { header: "Description ", field:"description"},
        { header: "Status", field: "status" },
      ]}
    />
  );
}