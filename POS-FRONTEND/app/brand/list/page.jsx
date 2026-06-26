"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonList from "../../Components/CommonList";
 
export default function BrandList() {
  const columns = [
    {
      label: "ID",
      field: "id",
    },
    {
      label: "Identifier",
      field: "identifier",
    },
    {
      label: "Description",
      field: "description",
    },
    {
      label: "Status",
      field: "status",
    },
  ];
 
  return (
    <Layout>
      <CommonList
        title="Brand Management"
        urlName="brand"
        columns={columns}
        showStatus={true}
      />
    </Layout>
  );
}