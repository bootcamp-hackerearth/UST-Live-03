"use client";

import CommonList from "@/components/table/CommonList";

const userColumns = [
  {
    header: "Name",
    field: "name",
  },

  {
    header: "Username",
    field: "username",
  },

  {
    header: "Phone Number",
    field: "phoneNo",
  },

  {
    header: "Roles",
    field: "roles",
    render: (row) => {
      if (!row.roles?.length) {
        return "-";
      }

      return row.roles.join(", ");
    },
  },
];

export default function UsersPage() {
  return (
    <CommonList
      title="Users"
      subtitle="Manage system users"
      entity="user"
      addPath="/users/add"
      editPath="/users/edit"
      columns={userColumns}
      showToggle={false}
      identifierField="username"
    />
  );
}