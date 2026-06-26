"use client";

import React from "react";
import EditPage from "../../../components/common/EditPage";

const sections = [
  {
    title: "Stock Information",
    columns: 2,
    fields: [
      {
        key: "identifier",
        label: "Stock Identifier",
        type: "text",
        disabled: true,
        required: true,
      },
      {
        key: "product",
        label: "Product",
        type: "text",
        disabled: true,
        required: true,
      },
      {
        key: "warehouse",
        label: "Warehouse",
        type: "text",
        disabled: true,
        required: true,
      },
      {
        key: "minimumStock",
        label: "Minimum Stock",
        type: "number",
        required: true,
      },
      {
        key: "quantity",
        label: "Quantity",
        type: "number",
        required: true,
      },
    ],
  },
];

function StockEdit() {
  return (
    <EditPage
      title="Edit Stock"
      routeName="stock"
      backUrl="/stock"
      sections={sections}
    />
  );
}

export default StockEdit;