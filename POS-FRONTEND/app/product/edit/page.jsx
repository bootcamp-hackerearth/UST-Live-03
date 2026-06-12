"use client";

import React from "react";
import CommonEdit from "../../Components/CommonEdit";
import Layout from "@/app/Components/Layout";

function EditProduct() {

  const extraFields = [
    {
      key: "productName",
      label: "Product Name",
      type: "text",
      placeholder: "Enter product name",
      required: true,
    },
    {
      key: "brand",
      label: "Brand",
      type: "select",
      apiPath: "brand",
      required: true,
    },
    {
      key: "models",
      label: "Model",
      type: "select",
      apiPath: "model",
      required: true,
    },
    {
      key: "category",
      label: "Category",
      type: "multiselect",
      apiPath: "category",
      valueFormat: "csv",
      required: true,
    },
    {
      key: "status",
      label: "Status",
      type: "select",
      valueType: "boolean",
      required: true,
      options: [
        {
          label: "Active",
          value: "true",
        },
        {
          label: "Inactive",
          value: "false",
        },
      ],
    },
  ];

  return (
    <Layout>
      <CommonEdit
        title="Product"
        apiPath="product"
        extraFields={extraFields}
        onSuccessPath="/product/list"
      />
      </Layout>
  );
}

export default EditProduct;
