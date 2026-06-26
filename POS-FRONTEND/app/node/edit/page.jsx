"use client";

import React from "react";
import Layout from "../../Components/Layout";
import CommonEdit from "../../Components/CommonEdit";

export default function EditNode() {
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
      <CommonEdit
        title="Node"
        apiPath="node"
        identifierParam="identifier"
        extraFields={extraFields}
        onSuccessPath="/node/list"
        showDescription = {false}

      />
    </Layout>
  );
}