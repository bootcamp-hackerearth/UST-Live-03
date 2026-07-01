"use client";

import CommonList from "../../components/CommonList";

export default function BrandPage() {
  return (
    <CommonList
      title="Brand Management"

      apiUrl="/api/brand/list"
      method="POST"

      deleteApi="/api/brand/delete"
      deleteParam="identifier"

      editRoute="/brand/edit/:identifier"
      addRoute="/brand/add"
      showStatus={true}
      toggleApi="/api/brand/toggle-status"
      toggleParam="identifier"
      toggleField="status"
      toggleMethod="PATCH"

      columns={[
        { header: "ID", field: "id" },
        { header: "Brand Name", field: "identifier" },
        { header: "Description", field: "description" },
        { header: "Status", field: "status" },
      ]}
    ></CommonList>
  );
}
