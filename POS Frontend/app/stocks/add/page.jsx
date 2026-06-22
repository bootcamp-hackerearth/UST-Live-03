"use client";

import React, { useState } from "react";
import AddFormSkeleton from "@/components/CommonAddForm";
import SingleDropdown from "@/components/dropdowns/CommonSingleDropdown";

const PRODUCT_STATUS_OPTIONS = [
  { value: "AVAILABLE", label: "Available" },
  { value: "OUT_OF_STOCK", label: "Out of Stock" },
  { value: "LOW_STOCK", label: "Low Stock" },
  { value: "INCOMING", label: "Incoming" },
  { value: "BLOCKED", label: "Blocked" },
  { value: "DAMAGED", label: "Damaged" },
];

export default function AddStocks() {
  const [identifier, setIdentifier] = useState("");
  const [wareHouse, setWareHouse] = useState("");
  const [productStatus, setProductStatus] = useState("");

  const extraFields = [
    { key: "identifier", type: "custom", component: <SingleDropdown label="SKU Code" apiUrl="/product/findByStatus" selectedValue={identifier} onChange={setIdentifier} /> },
    { key: "availableStock", label: "Available Stock", type: "number" },
    { key: "incomingStock", label: "Incoming Stock", type: "number" },
    { key: "outgoingStock", label: "Outgoing Stock", type: "number" },
    { key: "name", label: "Name", type: "text" },
    { key: "productStatus", type: "custom", component: <SingleDropdown label="Product Status" options={PRODUCT_STATUS_OPTIONS} selectedValue={productStatus} onChange={setProductStatus} /> },
    { key: "wareHouse", type: "custom", component: <SingleDropdown label="Warehouse" apiUrl="/wareHouse/findByStatus" selectedValue={wareHouse} onChange={setWareHouse} /> },
  ];

  return <AddFormSkeleton title="Stocks" apiPath="stocks" extraFields={extraFields} extraData={{ identifier, wareHouse, productStatus }} showIdentifier={false} />;
}