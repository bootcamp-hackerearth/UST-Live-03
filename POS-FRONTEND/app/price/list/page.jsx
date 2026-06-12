"use client";

import React from "react";
import Layout from "../../Components/Layout";
import CommonList from "../../Components/CommonList";

export default function PriceList() {
  const columns = [
    {
      label: "ID",
      field: "id",
    },
    {
      label: "Product",
      render: (item) =>
        item.product && (item.product.identifier || item.product.productName)
          ? `${item.product.identifier ?? item.product.id} - ${item.product.productName ?? item.product.name ?? item.product.identifier ?? item.product.id}`
          : item.productIdentifier || item.identifier || "-",
    },
    {
      label: "MRP",
      render: (item) => (
        <span className="bg-gray-500 text-black px-2 py-1 rounded text-xs">
          ₹{item.mrp}
        </span>
      ),
    },
    {
      label: "Selling Price",
      render: (item) => (
        <span className="bg-green-600 text-black px-2 py-1 rounded text-xs">
          ₹{item.sellingPrice}
        </span>
      ),
    },
    {
      label: "Effective From",
      render: (item) =>
        item.effectiveFrom
          ? new Date(item.effectiveFrom).toLocaleString()
          : "-",
    },
    {
      label: "Status",
      field: "status",
    },
  ];

  return (
    <Layout>
      <CommonList
        title="Price Management"
        columns={columns}
        urlName="price"
        showStatus={true}
      />
    </Layout>
  );
}