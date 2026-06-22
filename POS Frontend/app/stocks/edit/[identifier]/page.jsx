"use client";

import React, { useState } from "react";
import EditFormSkeleton from "@/components/CommonEditForm";
import SingleDropdown from "@/components/dropdowns/CommonSingleDropdown";

const PRODUCT_STATUS_OPTIONS = [
  { value: "AVAILABLE", label: "Available" },
  { value: "OUT_OF_STOCK", label: "Out of Stock" },
  { value: "LOW_STOCK", label: "Low Stock" },
  { value: "INCOMING", label: "Incoming" },
  { value: "BLOCKED", label: "Blocked" },
  { value: "DAMAGED", label: "Damaged" },
];

export default function EditStocks() {
  const [wareHouse, setWareHouse] = useState("");
  const [productStatus, setProductStatus] = useState("");

  const extraFields = [
    { key: "availableStock", label: "Available Stock", type: "number" },
    { key: "incomingStock", label: "Incoming Stock", type: "number" },
    { key: "outgoingStock", label: "Outgoing Stock", type: "number" },
    { key: "name", label: "Name", type: "text" },
    { key: "productStatus", type: "custom", component: <SingleDropdown label="Product Status" options={PRODUCT_STATUS_OPTIONS} selectedValue={productStatus} onChange={setProductStatus} /> },
    { key: "wareHouse", type: "custom", component: <SingleDropdown label="Warehouse" apiUrl="/wareHouse/findByStatus" selectedValue={wareHouse} onChange={setWareHouse} /> },
  ];

  return (
    <EditFormSkeleton
      title="Stocks"
      apiPath="stocks"
      extraFields={extraFields}
      extraData={{ wareHouse, productStatus }}
      setters={{ 
        wareHouse: setWareHouse, 
        productStatus: setProductStatus 
      }}
    />
  );
}