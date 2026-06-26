"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonList from "../../Components/CommonList";
 
export default function WarehouseList() {
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
      label: "Location",
      field: "location",
    },
    {
      label: "Manager",
      field: "manager",
    },
    {
      label: "Status",
      field: "status",
    },
  ];
 
  return (
    <Layout>
      <CommonList
        title="Warehouse Management"
        urlName="warehouse"
        columns={columns}
        showStatus={true}
      />
    </Layout>
  );
}