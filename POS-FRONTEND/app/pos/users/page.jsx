// app/pos/users/page.jsx

"use client";

import BaseListForm from "../../../components/lists/BaseListForm";

const UserList = () => {
  const columns = [
    { key: "id", label: "ID" },
    { key: "name", label: "Name" },
    { key: "username", label: "Email" },
    { key: "phoneNo", label: "Phone" },
    {
      key: "roles",
      label: "Roles",
      render: (val) => {
        if (!val || val.length === 0)
          return <span className="text-[#231F20]/40">No roles</span>;
        return (
          <div className="flex flex-wrap gap-1">
            {val.map(role => (
              <span
                key={role}
                className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-semibold bg-[#006E74]/10 text-[#006E74] border border-[#006E74]/20"
              >
                {role.replace("ROLE_", "").replace("_", " ")}
              </span>
            ))}
          </div>
        );
      }
    },
    { key: "status", label: "Status" }
  ];

  return (
    <BaseListForm
      title="Users"
      entity="user"
      columns={columns}
      addPath="/pos/users/add"
      editPath="/pos/users/edit"
      identifierKey="username"
    />
  );
};

export default UserList;