"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonAdd from "../../Components/CommonAdd";
 
export default function AddRack() {
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
      <CommonAdd
        title="Rack"
        apiPath="rack"
        extraFields={extraFields}
        onSuccessPath="/rack/list"
      />
    </Layout>
  );
}