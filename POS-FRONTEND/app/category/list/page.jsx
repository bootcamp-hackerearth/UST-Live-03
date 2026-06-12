"use client";

import React from "react";
import Layout from "../../Components/Layout";
import CommonList from "../../Components/CommonList";

export default function CategoryList() {
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
      label: "Super Category",
      field: "superCategory",
    },
    {
      label: "Status",
      field: "status",
    },
  ];

  return (
    <Layout>
      <CommonList
        title="Category Management"
        columns={columns}
        urlName="category"
        showStatus={true}
      />
    </Layout>
  );
}