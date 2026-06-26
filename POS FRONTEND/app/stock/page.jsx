"use client";
import ListTemplate from "../components/ListTemplate";
import { auditColumns } from "../components/auditColumns";

export default function StockList() {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Identifier", field: "identifier" },
    { label: "Product", field: "product" },
    { label: "Warehouse", field: "warehouse", render: (item) => Array.isArray(item.warehouse) && item.warehouse.length ? item.warehouse.join(", ") : "-" },
    { label: "Quantity", field: "quantity" },
    { label: "Status", field: "status" },
    ...auditColumns(),
  ];

  return (
    <ListTemplate
      title="Stock Management"
      columns={columns}
      urlName="stock"
      showStatus={true}
      editKey="identifier"
      deleteKey="identifier"
      deleteParam="identifier"
      statusKey="identifier"
      addButtonLabel="Stock"
      pageSize={10}
    />
  );
}