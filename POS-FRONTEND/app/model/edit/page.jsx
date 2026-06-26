"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonEdit from "../../Components/CommonEdit";
 
export default function EditModel() {
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
        title="Model"
        apiPath="model"
        identifierField="identifier"
        extraFields={extraFields}
        onSuccessPath="/model/list"
      />
    </Layout>
  );
}