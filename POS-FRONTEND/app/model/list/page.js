"use client";

import CommonList from "@/app/components/CommonList";

export default function ModelPage() {
  return (
    <CommonList
      title="Model Management"
      apiUrl="/api/model/list"
      method="POST"

      deleteApi="/api/model/delete"
      deleteParam="identifier"

      editRoute="/model/edit/:identifier"
      addRoute="/model/add"

      showStatus={true}
      toggleApi="/api/model/toggle-status"
      toggleParam="identifier"
      toggleField="status"
      toggleMethod="PATCH"

      columns={[
        {
          header: "Sl No",
          field: "id",
        },
        {
          header: "Model Name",
          field: "identifier",
        },
        {
          header: "Status",
          field: "status",
        },
      ]}
    />
  );
}