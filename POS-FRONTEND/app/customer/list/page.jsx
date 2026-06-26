"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonList from "../../Components/CommonList";
 
export default function CustomerList() {
  const columns = [
    {
      label: "ID",
      field: "id",
    },
    {
      label: "Customer Name",
      field: "customerName",
    },
    {
      label: "Identifier",
      field: "identifier",
    },
    {
      label: "Party Type",
      field: "partyType",
    },
    {
      label: "Credit Limit",
      field: "creditLimit",
      type: "currency",
    },
    {
      label: "Balance",
      field: "balance",
      type: "currency",
    },
    {
      label: "Balance Type",
      field: "balanceType",
    },
    {
      label: "Status",
      field: "status",
    },
  ];
 
  return (
    <Layout>
      <CommonList
        title="Customer Management"
        urlName="customer"
        columns={columns}
        showStatus={true}
      />
    </Layout>
  );
}