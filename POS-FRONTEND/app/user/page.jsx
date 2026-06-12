"use client";

import ListTemplate from "../components/ListTemplate";

export default function Page() {
  const columns = [
    {
      label: "Username",
      field: "username",
    },
    {
      label: "Name",
      field: "name",
    },
    {
      label: "Phone Number",
      field: "phoneNo",
    },
    {
      label: "Roles",
      field: "roles",
      render: (item) =>
        Array.isArray(item.roles)
          ? item.roles.join(", ")
          : item.roles || "-",
    },
  ];

  return (
    <ListTemplate
      title="User Management"
      columns={columns}
      urlName="user"
      showStatus={false}
      editKey="username"
      deleteKey="username"
      deleteParam="username"
      rowKey="username"
      addButtonLabel="User"
      pageSize={10}
      sortField="username"
      editUseQuery={true}
    />
  );
}