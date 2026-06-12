"use client";
import React from "react";
import CommonAdd from "../../Components/CommonAdd";
import Layout from "@/app/Components/Layout";
 
function AddProduct() {
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
      <CommonAdd
        title="Product"
        apiPath="product"
        extraFields={extraFields}
        onSuccessPath="/product/list"
      />
   </Layout>
  );
}
 
export default AddProduct;
