"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonEdit from "../../Components/CommonEdit";
 
export default function EditRack() {
  const extraFields = [
   
    {
      key: "shelfs",
      label: "Shelf",
      type: "select",
      apiPath: "shelfs",
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
        title="Rack"
        apiPath="rack"
        extraFields={extraFields}
        identifierField="identifier"
        onSuccessPath="/rack/list"
      />
    </Layout>
  );
}