"use client";
import React from "react";
import CommonList from "../../Components/CommonList";
import Layout from "../../Components/Layout";

const UserList = () => {

  const columns = [

    {
      label: "ID",
      field: "id",
    },

    {
      label: "Username",
      field: "username",
    },

    {
      label: "Name",
      field: "name",
    },

    {
      label: "Phone",
      field: "phoneNo",
    },

    {
      label: "Roles",
      field: "roles",
      render: (item) => (

        <div className="flex flex-wrap gap-2">

          {item.roles && item.roles.length > 0 ? (

            item.roles.map((role) => {
              const roleLabel = typeof role === "object" ? role.identifier : role;
              const roleKey = `${item.username || item.id}-${String(roleLabel)}`;

              return (
                <span
                  key={roleKey}
                  className="rounded-full bg-indigo-100 px-3 py-1 text-xs font-semibold text-indigo-700"
                >
                  {roleLabel}
                </span>
              );
            })

          ) : (

            <span className="text-slate-400">
              No Roles
            </span>

          )}

        </div>

      ),
    },

    {
      label: "Status",
      field: "status",
    },

  ];

  return (

    <Layout>

      <CommonList
        title="User Management"
        columns={columns}
        urlName="user"
        showStatus={true}
      />

    </Layout>

  );
};

export default UserList;
