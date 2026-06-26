"use client";

import CommonList from "@/app/components/CommonList";

export default function StockListPage() {
  const columns = [
    { header: "ID", field: "id" },
    { header: "Identifier", field: "identifier" },
    { header: "Product", field: "productIdentifier" },
    { header: "Warehouse", field: "warehouseIdentifier" },
    { header: "Quantity", field: "quantity" },
    { header: "Min Stock", field: "minimumStock" },
    {
  header: "Status",
  field: "statusLabel",
  render: (row) => {
    const statusClasses = {
      IN_STOCK: "bg-green-100 text-green-700",
      LOW_STOCK: "bg-yellow-100 text-yellow-700",
      OUT_OF_STOCK: "bg-red-100 text-red-700",
    };

    const statusClass =
      statusClasses[row.statusLabel] || "bg-red-100 text-red-700";

    return (
      <span
        className={`px-2 py-1 rounded-full text-xs font-semibold ${statusClass}`}
      >
        {row.statusLabel?.replace("_", " ")}
      </span>
    );
  },
}
  ];

  return (
    <CommonList
      title="Stock Management"
      apiUrl="/api/stock/list"
      addRoute="/stock/add"
      editRoute="/stock/edit/:identifier"
      deleteApi="/api/stock/deleteByIdentifier" 
      deleteParam="identifier"
      columns={columns}
    />
  );
}