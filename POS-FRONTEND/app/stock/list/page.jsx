"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonList from "../../Components/CommonList";
 
export default function StockList() {
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
      label: "Warehouse",
      field: "warehouse",
    },
    {
      label: "Quantity",
      field: "quantity",
    },
    {
      label: "Unit",
      field: "unit",
    },
    {
      label: "Status",
      field: "status",
    },
    
  ];
 
  return (
    <Layout>
      <CommonList
        title="Stock Management"
        urlName="stock"
        columns={columns}
        showStatus={true}

      />
    </Layout>
  );
}