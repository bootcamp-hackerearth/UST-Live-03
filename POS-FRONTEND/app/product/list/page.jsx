"use client";
import React from "react";
import List from "../../../components/List";

function ProductList() {
  const columns = [
    {
      key: "id",
      label: "ID",
    },

    {
      key: "productName",
      label: "PRODUCT NAME",
    },

    {
      key: "identifier",
      label: "IDENTIFIER",
    },

    {
      key: "brand",
      label: "BRAND",
    },

    {
      key: "category",
      label: "CATEGORY",
    },

    {
      key: "model",
      label: "MODEL",
    },

    {
      key: "unit",
      label: "UNIT",
    },
  ];

  return (
    <List
      title="PRODUCT"
      apiPath="product"
      columns={columns}
      addPath="/product/add"
      editPath="/product/edit"
      showStatusToggle={true}
    />
  );
}
export default ProductList;
