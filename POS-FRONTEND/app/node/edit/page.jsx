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
  ];

  return (
    <Layout>
      <CommonEdit
        title="Node"
        apiPath="node"
        identifierParam="identifier"
        extraFields={extraFields}
        onSuccessPath="/node/list"
      />
    </Layout>
  );
}