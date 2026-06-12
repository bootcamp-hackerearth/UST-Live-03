"use client";

import React from "react";
import Layout from "../../Components/Layout";
import CommonAdd from "../../Components/CommonAdd";

export default function AddPrice() {
  const extraFields = [
    {
      key: "identifier",
      label: "Identifier",
      type: "select",
      apiPath: "product",
      valueField: "identifier",
      labelField: "identifier",
      required: true,
    },
   
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
      <CommonAdd
        title="Price"
        apiPath="price"
        extraFields={extraFields}
        showIdentifierDescription={false}
        onSuccessPath="/price/list"

      />
    </Layout>
  );
}
