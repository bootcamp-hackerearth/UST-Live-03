"use client";

import React from "react";
import CommonList from "@/components/CommonList";

export default function UserList() {
  const fields = [
    {
      name: "username",
      type: "email",
      placeholder: "Enter Email",
      required: true,
    },
    {
      name: "name",
      type: "text",
      placeholder: "Enter Name",
      required: true,
    },
    {
      name: "phoneNo",
      type: "text",
      placeholder: "Enter Phone Number",
      required: true,
    },
    {
      name: "password",
      type: "password",
      placeholder: "Enter Password",
      required: true,
    },
    {
      name: "roles",
      type: "select",
      placeholder: "Select Roles",
      dataKey: "roles",
      hardCoded: false,
      multiple: true,
      required: true,
    },
  ];

  const dropdownApis = {
    roles: "http://localhost:8080/api/role/list",
  };

  const transformPayload = (data) => ({
    ...data,
    roles: Array.isArray(data.roles)
      ? data.roles.map((role) =>
          typeof role === "object"
            ? role.identifier
            : role
        )
      : [],
  });

  return (
    <CommonList
      title="Users"
      subtitle="Manage all users and roles"
      apiUrl="http://localhost:8080/api/user/list"
      deleteUrl="http://localhost:8080/api/user/delete"
      addUrl="http://localhost:8080/api/user/add"
      updateUrl="http://localhost:8080/api/user/update"
      apiRoute="user"
      fields={fields}
      dropdownApis={dropdownApis}
      transformPayload={transformPayload}
      searchKeys={["name", "username", "phoneNo"]}
      columns={[
        {
          key: "username",
          label: "Email",
        },
        {
          key: "name",
          label: "Name",
        },
        {
          key: "phoneNo",
          label: "Phone",
        },
        {
          key: "roles",
          label: "Roles",
          render: (item) => (
            <div className="flex flex-wrap gap-2">
              {item.roles?.map((role, index) => (
                <span
                  key={`${role}-${index}`}
                  className="px-3 py-1 rounded-full bg-[#fff7f2] border border-[#f4dfd2] text-[#c17a47] text-xs font-medium"
                >
                  {role}
                </span>
              ))}
            </div>
          ),
        },
      ]}
    />
  );
}