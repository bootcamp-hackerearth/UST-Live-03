"use client";

import React from "react";
import Layout from "../../Components/Layout";
import CommonList from "../../Components/CommonList";

export default function RoleList() {
  const columns = [
    {
      label: "ID",
      field: "id",
    },
    {
      label: "Role",
      field: "identifier",
    },
    {
      label: "Status",
      field: "status",
    },
  ];

  return (
    <Layout>
      <CommonList
        title="Role Management"
        columns={columns}
        urlName="role"
        showStatus={true}
      />
    </Layout>
  );
}