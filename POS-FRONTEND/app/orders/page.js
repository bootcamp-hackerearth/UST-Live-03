"use client";

import React from "react";
import Link from "next/link";
import CommonList from "@/app/components/CommonList";

export default function OrdersListPage() {
  const columns = [
    {
      label: "Invoice Code",
      field: "invoiceCode",
      render: (item) => (
        <Link 
          href={`/orders/${item.identifier}`}
          className="text-blue-600 hover:text-blue-800 font-bold hover:underline tracking-wide font-mono"
        >
          📄 {item.invoiceCode}
        </Link>
      ),
    },
    { label: "Customer", field: "customerIdentifier" },
    { label: "Warehouse", field: "warehouseIdentifier" },
    { 
      label: "Total Price", 
      field: "totalPrice",
      render: (item) => <span>${Number(item.totalPrice || 0).toFixed(2)}</span>
    },
    { label: "Payment Method", field: "paymentMethod" },
    { 
      label: "Date", 
      field: "createdOn",
      render: (item) => <span>{item.createdOn ? new Date(item.createdOn).toLocaleDateString() : "N/A"}</span>
    }
  ];

  return (
    <CommonList
      title="Orders History Tracking"
      columns={columns}
      urlName="orders" 
      editKey="identifier"
      showStatus={false}
    />
  );
}