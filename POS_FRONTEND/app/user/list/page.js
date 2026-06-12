"use client";
 
import CommonList from "../../components/CommonList";
 
export default function UserPage() {
  return (
    <CommonList
      title="User Management"
 
      // ✅ API
      apiUrl="/api/user/list"
      method="POST"
 
      // ✅ ✅ FIX: use username (NOT identifier)
      deleteApi="/api/user/delete"
      deleteParam="username"
 
      // ✅ ✅ FIX: route should use username
      editRoute="/user/edit/:username"
      addRoute="/user/add"
 
      columns={[
        { header: "ID", field: "id" },
        { header: "Username", field: "username" },
        { header: "Name", field: "name" },
        { header: "Phone No", field: "phoneNo" },
 
        // ✅ Roles (List<String>)
        {
          header: "Roles",
          field: "roles",
          render: (row) => row.roles?.join(", ") || "-",
        },
      ]}
    />
  );
}