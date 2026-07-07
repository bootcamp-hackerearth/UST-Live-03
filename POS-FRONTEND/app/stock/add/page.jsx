"use client";

import React, { useState } from "react";
import Add from "../../../components/add";
import SingleDropdown from "../../../components/SingleDropdown";

function StockAdd() {
  const [productName, setProductName] = useState("");
  const [warehouseName, setWarehouseName] = useState("");
  const [stockStatus, setStockStatus] = useState("IN_STOCK");

  const extraFields = [
    {
      key: "productName",
      type: "custom",
      component: (
        <SingleDropdown
          value={productName}
          label="Product"
          apiPath="product/getAllActive"
          valueField="productName"
          displayField="productName"
          onChange={setProductName}
          urlMethod={"get"}
        />
      ),
    },
    {
      key: "warehouseName",
      type: "custom",
      component: (
        <SingleDropdown
          value={warehouseName}
          label="Warehouse"
          apiPath="warehouse/list"
          required
          onChange={setWarehouseName}
          urlMethod={"post"}
        />
      ),
    },
    {
      key: "quantity",
      label: "Quantity",
      type: "number",
      required: true,
    },
    {
      key: "stockStatus",
      type: "custom",
      required: true,
      component: (
        <select
          value={stockStatus}
          onChange={(e) => setStockStatus(e.target.value)}
          required
          style={{
            width: "100%",
            padding: "10px 14px",
            border: "1px solid #d1d5db",
            borderRadius: "6px",
          }}
        >
          <option value="IN_STOCK">In Stock</option>
          <option value="OUT_OF_STOCK">Out Of Stock</option>
        </select>
      ),
    },
  ];

  return (
    <Add
      title="Stock"
      apiPath="stock"
      extraFields={extraFields}
      extraData={{
        productName,
        warehouseName,
        stockStatus,
      }}
    />
  );
}

export default StockAdd;