"use client";

import React from "react";
import Layout from "../../Components/Layout";
import CommonAdd from "../../Components/CommonAdd";

export default function AddNode() {
  const extraFields = [
    {
      key: "path",
      label: "Path",
      type: "text",
      placeholder: "/example/path",
      required: true,
    },
    {
      key: "roles",
      label: "Roles",
      type: "multiselect",
      apiPath: "role",
      required: true,
    },
    {
      key: "status",
      label: "Status",
      type: "select",
      valueType: "boolean",
      required: true,
      options: [
        {
          label: "Active",
          value: "true",
        },
        {
          label: "Inactive",
          value: "false",
        },
      ],
    },
  ];

  return (
    <Layout>
      <CommonAdd
        title="Node"
        apiPath="node"
        extraFields={extraFields}
        onSuccessPath="/node/list"
      />
    </Layout>
  );
}