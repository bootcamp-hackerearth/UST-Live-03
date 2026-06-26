"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonList from "../../Components/CommonList";
 
export default function ModelList() {
  const columns = [
    {
      label: "ID",
      field: "id",
    },
    {
      label: "Model Name",
      field: "identifier",
    },
    {
      label: "Status",
      field: "status",
    },
  ];
 
  return (
    <Layout>
      <CommonList
        title="Model Management"
        urlName="model"
        columns={columns}
        showStatus={true}
      />
    </Layout>
  );
}