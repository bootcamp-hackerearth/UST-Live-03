"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonList from "../../Components/CommonList";
 
export default function ShelfList() {
  const columns = [
    {
      field: "id",
      label: "ID",
    },
    {
      field: "identifier",
      label: "Shelf Identifier",
    },
    {
      field: "status",
      label: "Status",
      type: "toggle",
    },
  ];
 
  return (
    <Layout>
      <CommonList
        title="Shelf Management"
        urlName="shelfs"
        columns={columns}
        showStatus={true}
      />
    </Layout>
  );
}