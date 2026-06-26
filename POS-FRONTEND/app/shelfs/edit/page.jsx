"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonEdit from "../../Components/CommonEdit";
 
export default function EditShelf() {
  const extraFields = [
    {
      key: "identifier",
      label: "Shelf Name",
      type: "text",
      placeholder: "Enter shelf name",
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
        title="Shelf"
        apiPath="shelfs"
        extraFields={extraFields}
        backPath="/shelfs/list"
      />
    </Layout>
  );
}