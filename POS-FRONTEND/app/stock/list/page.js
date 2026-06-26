"use client";

import CommonList from "../../components/CommonList";

export default function StockPage() {
  return (
    <CommonList
      title="Stock Management"
      apiUrl="/api/stock/list"
      deleteApi="/api/stock/delete"
      editRoute="/stock/edit/:identifier"
      addRoute="/stock/add"
      deleteParam="identifier"
      columns={[
        {
          header: "ID",
          field: "id",
        },
        {
          header: "Identifier",
          field: "identifier",
        },
        {
          header: "Product",
          field: "productIdentifier",
        },
        {
          header: "Warehouse",
          field: "warehouseIdentifier",
        },
        {
          header: "Quantity",
          field: "quantity",
        },
        {
          header: "Min Stock",
          field: "minimumStock",
        },
        {
  header: "Status",
  field: "status",
  render: (row) => {
    let label = "";
    let bgColor = "";
    let textColor = "";

    if (row.quantity === 0) {
      label = "OUT OF STOCK";
      bgColor = "#dc2626";
      textColor = "#ffffff";
    } else if (row.quantity < row.minimumStock) {
      label = "LOW STOCK";
      bgColor = "#f59e0b";
      textColor = "#000000";
    } else {
      label = "IN STOCK";
      bgColor = "#16a34a";
      textColor = "#ffffff";
    }

    return (
      <span
        style={{
          minWidth: "120px",
          display: "inline-block",
          textAlign: "center",
          padding: "6px 12px",
          fontSize: "12px",
          fontWeight: "600",
          borderRadius: "9999px",
          backgroundColor: bgColor,
          color: textColor,
        }}
      >
        {label}
      </span>
    );
  },
},
      ]}
    />
  );
}