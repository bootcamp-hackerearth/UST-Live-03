"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonEdit from "../../Components/CommonEdit";
 
export default function EditUnit() {
  const extraFields = [
   
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
        title="Unit"
        apiPath="unit"
        extraFields={extraFields}
        identifierField="identifier"
        backPath="/unit/list"
      />
    </Layout>
  );
}