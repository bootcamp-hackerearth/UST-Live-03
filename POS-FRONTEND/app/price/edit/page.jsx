"use client";

import React from "react";
import Layout from "../../Components/Layout";
import CommonEdit from "../../Components/CommonEdit";

export default function EditPrice() {
  const extraFields = [
   
    {
      key: "mrp",
      label: "MRP",
      type: "number",
      placeholder: "Enter MRP",
      required: true,
    },
    {
      key: "sellingPrice",
      label: "Selling Price",
      type: "number",
      placeholder: "Enter Selling Price",
      required: true,
    },
    {
      key: "effectiveFrom",
      label: "Effective From",
      type: "datetime-local",
      required: true,
    },
  ];

  return (
    <Layout>
      <CommonEdit
        title="Price"
        apiPath="price"
        extraFields={extraFields}
        onSuccessPath="/price/list"
        showDescription={false}
      />
    </Layout>
  );
}
