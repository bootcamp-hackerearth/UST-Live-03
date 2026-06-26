"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonList from "../../Components/CommonList";
 
export default function RackList() {
  const columns = [
    {
      label: "Rack Identifier",
      field: "identifier",
    },
    {
      label: "Shelf",
      field: "shelfs",
    },
    {
      label: "Status",
      field: "status",
    },
  ];
 
  return (
    <Layout>
      <CommonList
        title="Rack Management"
        urlName="rack"
        columns={columns}
        showStatus={true}
      />
    </Layout>
  );
}