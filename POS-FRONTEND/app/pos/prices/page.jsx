// app/pos/prices/page.jsx

"use client";

import BaseListForm from "../../../components/lists/BaseListForm";

export default function PriceListPage() {
  const columns = [
    { key: "id", label: "ID" },
    { key: "identifier", label: "Product" },
    { 
      key: "mrp", 
      label: "MRP",
      render: (value) => value ? `₹${Number.parseFloat(value).toFixed(2)}` : "-"
    },
    { 
      key: "sellingPrice", 
      label: "Selling Price",
      render: (value) => value ? `₹${Number.parseFloat(value).toFixed(2)}` : "-"
    },
    { 
      key: "costPrice", 
      label: "Cost Price",
      render: (value) => value ? `₹${Number.parseFloat(value).toFixed(2)}` : "-"
    },
    { 
      key: "effectiveFrom", 
      label: "Effective From",
      render: (value) => value ? new Date(value).toLocaleDateString() : "-"
    },
    { key: "status", label: "Status" },
  ];

  return (
    <BaseListForm
      title="Prices"
      entity="price"
      columns={columns}
      addPath="/pos/prices/add"
      editPath="/pos/prices/edit"
      identifierKey="identifier"
    />
  );
}