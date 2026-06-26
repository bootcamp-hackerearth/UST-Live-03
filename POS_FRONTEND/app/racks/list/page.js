"use client";
import CommonList from "@/app/components/CommonList";

export default function RackListPage() {
  const columns = [
    { header: "ID", field: "id" },
    { header: "Rack Name", field: "identifier" },
    { header: "Shelf", field: "shelfs" },
    { header: "Status", field: "status" },
  ];

  return (
    <CommonList
      title="Manage Racks"
      apiUrl="/api/racks/list"
      columns={columns}
      deleteApi="/api/racks/delete"
      deleteParam="identifier"
      editRoute="/racks/edit/:identifier"
      addRoute="/racks/add"
      showStatus={true}
      toggleApi="/api/racks/toggle-status"
      toggleField="status"
    />
  );
}