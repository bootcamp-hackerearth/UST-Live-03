"use client";

import CommonAddPage from "@/app/components/CommonAddPage";
import api from "@/app/services/api";

export default function AddStockPage() {
  const fields = [
    { label: "Product", name: "productIdentifier", type: "dropdown", api: "/api/product/findallactive", optionLabel: "identifier", optionValue: "identifier" },
    { label: "Warehouse", name: "warehouseIdentifier", type: "dropdown", api: "/api/warehouse/findallactive", optionLabel: "identifier", optionValue: "identifier" },
    { label: "Quantity", name: "quantity", type: "number" },
    { label: "Minimum Stock", name: "minimumStock", type: "number" },
  ];

  return (
    <CommonAddPage
      title="Add Stock"
      fields={fields}
      redirectRoute="/stock/list"
      initialValues={{ productIdentifier: "", warehouseIdentifier: "", quantity: 0, minimumStock: 0 }}
      submitApi={(data) =>
        api.post("/api/stock/add", {
          ...data,
          quantity: Number(data.quantity),
          minimumStock: Number(data.minimumStock),
        })
      }
    />
  );
}