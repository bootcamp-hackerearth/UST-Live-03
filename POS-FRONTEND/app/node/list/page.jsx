"use client";

import React from "react";
import Layout from "../../Components/Layout";
import CommonList from "../../Components/CommonList";

export default function NodeList() {
  const columns = [
    {
      label: "ID",
      field: "id",
    },
    {
      label: "Identifier",
      field: "identifier",
    },
    {
      label: "Path",
      field: "path",
    },
    {
      label: "Roles",
      render: (item) => {
        let roles = [];

        if (Array.isArray(item.roles)) {
          roles = item.roles;
        } else if (item.roles) {
          roles = String(item.roles).split(",");
        }

        return (
          <div className="flex flex-wrap gap-1">
            {roles.map((role) => {
              const trimmedRole = String(role).trim();
              const roleKey = trimmedRole || `role-${Math.random().toString(36).slice(2, 8)}`;

              return (
                <span
                  key={roleKey}
                  className="bg-blue-100 text-blue-700 px-2 py-1 rounded text-xs"
                >
                  {trimmedRole}
                </span>
              );
            })}
          </div>
        );
      },
    },
    {
      label: "Status",
      field: "status",
    },
  ];

  return (
    <Layout>
      <CommonList
        title="Node Management"
        columns={columns}
        urlName="node"
        showStatus={true}
      />
    </Layout>
  );
}