"use client";
import ListTemplate from "../components/ListTemplate";
import { auditColumns } from "../components/auditColumns";

function checkPreDelete(username) {
  const loggedIn = localStorage.getItem("username");
  if (username === loggedIn) return "Cannot delete the currently logged-in user.";
  return null;
}

export default function UserList() {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Username", field: "username" },
    { label: "Name", field: "name" },
    { label: "Phone", field: "phoneNo" },
    {
      label: "Roles",
      render: (item) => item.roles?.join(", ") || "-",
    },
    { label: "Status", field: "status" },
    ...auditColumns(),
  ];

  return (
    <ListTemplate
      title="User Management"
      columns={columns}
      urlName="user"
      showStatus={true}
      editKey="username"
      deleteKey="username"
      deleteParam="username"
      rowKey="username"
      statusKey="username"
      addButtonLabel="User"
      pageSize={10}
      sortField="username"
      onPreDelete={checkPreDelete}
    />
  );
}