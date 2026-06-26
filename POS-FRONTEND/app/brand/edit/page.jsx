"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonEdit from "../../Components/CommonEdit";
 
export default function EditBrand() {
  const fields = [
    {
      key: "identifier",
      label: "Identifier",
      type: "text",
      readOnly: true,
    },
    {
      key: "description",
      label: "Description",
      type: "textarea",
      required: true,
    },
    {
      key: "status",
      label: "Status",
      type: "select",
      valueType: "boolean",
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
        title="Brand"
        apiPath="brand"
        fields={fields}
        identifierField="identifier"
        onSuccessPath="/brand/list"
      />
    </Layout>
  );
}