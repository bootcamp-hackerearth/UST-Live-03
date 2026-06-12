"use client";
import React from "react";
import CommonList from "@/app/components/CommonList" 

const UserList = () => {
  const columns = [
    { label: "ID", field: "id" },
    { label : "Identifier", field: "identifier"},
    { label: "Name", field: "name" },
    { label: "Phone No", field: "phoneNo" },
    {
      label: "Roles",
      render: (item) =>
        item.roles?.map((r) => (
          <span
            key={r}
            className="bg-purple-100 text-purple-600 px-2 py-1 rounded text-xs mr-1"
          >
            {r}
          </span>
        )),
    },
    { label: "Username", field: "username" },
    { label: "Status", field: "status" },
  ];

  return (
    <CommonList
      title="User Management"
      columns={columns}
      urlName="user"
      showStatus={true}
      editKey="username"   
    />
  );
};

export default UserList;