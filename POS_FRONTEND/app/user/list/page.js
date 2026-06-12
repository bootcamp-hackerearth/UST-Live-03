"use client";
 
import CommonList from "../../components/CommonList";
 
export default function UserPage() {
  return (
    <CommonList
      title="User Management"
 
      apiUrl="/api/user/list"
      method="POST"
 
      deleteApi="/api/user/delete"
      deleteParam="username"
 
      editRoute="/user/edit/:username"
      addRoute="/user/add"
 
      columns={[
        { header: "ID", field: "id" },
        { header: "Username", field: "username" },
        { header: "Name", field: "name" },
        { header: "Phone No", field: "phoneNo" },
 
        {
          header: "Roles",
          field: "roles",
          render: (row) => row.roles?.join(", ") || "-",
        },
      ]}
    />
  );
}