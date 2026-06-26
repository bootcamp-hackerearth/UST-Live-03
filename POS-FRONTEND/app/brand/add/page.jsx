"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonAdd from "../../Components/CommonAdd";
 
export default function AddBrand() {
  const extraFields = [
    {
      key: "description",
      label: "Description",
      type: "textarea",
      placeholder: "Enter detailed description...",
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
        title="Brand"
        apiPath="brand"
        extraFields={extraFields}
        onSuccessPath="/brand/list"
      />
    </Layout>
  );
}
 