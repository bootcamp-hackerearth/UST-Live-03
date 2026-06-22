"use client";

import ListingSkeleton from "@/components/CommonListForm";

export default function ListStocks() {
  return (
    <ListingSkeleton
      title="Stocks"
      fields={["name", "availableStock", "outgoingStock", "incomingStock", "productStatus", "wareHouse"]}
      apis={{
        list: "/stocks/list",
        delete: "/stocks/delete",
        toggleStatus: "/stocks/toggle-status",
      }}
      addPath="/stocks/add"
      editPathBase="/stocks/edit/"
      paramKey="identifier"
      deleteStyle="param"
    />
  );
}