"use client";

import React from "react";
import ListPage from "../components/common/ListPage";

const columns = [
  { key: "identifier", label: "Stock Identifier", type: "text" },
  { key: "product", label: "Product", type: "text" },
  { key: "warehouse", label: "Warehouse", type: "text" },
  { key: "minimumStock", label: "Minimum Stock", type: "text" },
  { key: "quantity", label: "Quantity", type: "text" },
  { key: "stockStatus", label: "Status", type: "stockStatus" },
];

function StockList() {
  return (
    <ListPage
      title="Stock List"
      routeName="stock"
      columns={columns}
      addUrl="/stock/add"
      editUrl="/stock/edit"
    />
  );
}

export default StockList;