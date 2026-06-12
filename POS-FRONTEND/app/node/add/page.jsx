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