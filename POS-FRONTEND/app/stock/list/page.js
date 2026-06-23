"use client";

import React from "react";
import CommonList from "@/app/components/CommonList"; 

const StockList = () => {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Identifier", field: "identifier" },
    { label: "Product", field: "productIdentifier" },
    { label: "Warehouse", field: "warehouseIdentifier" },
    { label: "Qty", field: "quantity" },
    { label: "Min Qty", field: "minimumStock" },
    {
      label: "Stock Level",
      field: "stocklevel", 
      render: (item) => {
        if (item.quantity === 0) {
          return (
            <span className="inline-flex px-2 py-0.5 rounded-full text-xs font-bold bg-rose-50 text-rose-600 border border-rose-100">
              OUT
            </span>
          );
        // FIX: Compare item.quantity with item.minimumStock
        } else if (item.quantity < item.minimumStock) { 
          return (
            <span className="inline-flex px-2 py-0.5 rounded-full text-xs font-bold bg-amber-50 text-amber-600 border border-amber-100">
              LOW
            </span>
          );
        }
        return (
          <span className="inline-flex px-2 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-600 border border-emerald-100">
            OK
          </span>
        );
      },
    },
    { label: "Status", field: "status" }, 
  ];

  return (
    <CommonList
      title="Stock Management"
      columns={columns}
      urlName="stock"
      showStatus={true}
      editKey="identifier"
    />
  );
};

export default StockList;