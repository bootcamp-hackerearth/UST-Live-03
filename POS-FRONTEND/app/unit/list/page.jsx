 
"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonList from "../../Components/CommonList";
 
export default function UnitList() {
  const columns = [
    {
      field: "id",
      label: "ID",
    },
    {
      field: "identifier",
      label: "Unit Name",
    },
    {
      field: "status",
      label: "Status",
    },
  ];
 
  return (
    <Layout>
      <CommonList
        title="Unit Management"
        urlName="unit"
        columns={columns}
        showStatus={true}
      />
    </Layout>
  );
}