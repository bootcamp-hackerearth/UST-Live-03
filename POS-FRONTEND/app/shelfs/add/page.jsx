"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonAdd from "../../Components/CommonAdd";
 
export default function AddShelf() {
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
      <CommonAdd
        title="Shelf"
        apiPath="shelfs"
        extraFields={extraFields}
        onSuccessPath="/shelfs/list"
      />
    </Layout>
  );
}