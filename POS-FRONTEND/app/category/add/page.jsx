"use client";

import React from "react";
import Layout from "../../Components/Layout";
import CommonAdd from "../../Components/CommonAdd";

export default function AddCategory() {
  const extraFields = [
    {
      key: "superCategory",
      label: "Parent Super Category",
      type: "multiselect",
      apiPath: "category",
      valueFormat: "csv",
      required: false,
    },
  ];

  return (
    <Layout>
      <CommonAdd
        title="Category"
        apiPath="category"
        extraFields={extraFields}
        onSuccessPath="/category/list"
      />
    </Layout>
  );
}