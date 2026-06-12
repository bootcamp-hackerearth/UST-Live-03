"use client";
 
import CommonList from "../../components/CommonList";
 
export default function NodePage() {
  return (
    <CommonList
      title="Node Management"
 
      apiUrl="/api/node/list"
      method="POST"
 
      deleteApi="/api/node/delete"
      deleteParam="identifier"
 
      editRoute="/node/edit/:identifier"
      addRoute="/node/add"
     
      columns={[
        { header: "ID", field: "id" },
        { header: "Identifier", field: "identifier" },
        { header: "Path", field: "path" },
 
        {
          header: "Roles",
          field: "roles",
          render: (row) => row.roles?.join(", ") || "-", 
        },
      ]}
    />
  );
}