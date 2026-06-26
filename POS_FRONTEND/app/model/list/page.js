"use client";

import CommonList from "@/app/components/CommonList";

export default function ModelListPage() {
  const columns = [
    { header: "ID", field: "id"},
    { header: "Model Name", field: "identifier" },
    { header: "Status", field: "status" }
  ];

  return (
    <CommonList
      title="Model Management"
      apiUrl="/api/model/list"
      addRoute="/model/add"
      editRoute="/model/edit/:identifier"
      deleteApi="/api/model/delete"
      deleteParam="identifier"
      showStatus={true}
      toggleApi="/api/model/toggle-status"
      toggleParam="identifier"
      toggleField="status"
      columns={columns}
    />
  );
}