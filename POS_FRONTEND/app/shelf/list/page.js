"use client";

import CommonList from "@/app/components/CommonList";

export default function ShelfListPage() {
  const columns = [
    { header: "ID", field: "id" },
    { header: "Shelf Name", field: "identifier" },
    { header: "Status", field: "status" }
  ];

  return (
    <CommonList
      title="Shelf Management"
      apiUrl="/api/shelf/list"
      addRoute="/shelf/add"
      editRoute="/shelf/edit/:identifier"
      deleteApi="/api/shelf/delete"
      deleteParam="identifier"
      showStatus={true}
      toggleApi="/api/shelf/toggle-status"
      toggleParam="identifier"
      toggleField="status"
      columns={columns}
    />
  );
}