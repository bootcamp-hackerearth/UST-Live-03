"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";

export default function EditStockPage() {
  const fields = [
    { label: "Product", name: "productIdentifier", type: "text", readOnly: true },
    { label: "Warehouse", name: "warehouseIdentifier", type: "text", readOnly: true },
    { label: "Quantity", name: "quantity", type: "number" },
    { label: "Minimum Stock", name: "minimumStock", type: "number" },
    { label: "Current Status", name: "statusLabel", type: "text", readOnly: true } 
  ];

  const fetchStock = async (identifier) => {
    const res = await api.get("/api/stock/get", { params: { identifier } });
    return res.data;
  };

  return (
    <CommonEditPage
      title="Update Stock"
      fetchApi={fetchStock}
      updateApi="/api/stock/update"
      redirectRoute="/stock/list"
      fields={fields}
      identifierParam="identifier" 
    />
  );
}