"use client";

import ListingSkeleton from "@/components/CommonListForm";

export default function ListCustomer() {
  return (
    <ListingSkeleton
      title="Customers"
      fields={["customerName", "email", "partyType", "credit"]}
      apis={{
        list: "/customer/list",
        delete: "/customer/delete",
        toggleStatus: "/customer/toggle-status",
      }}
      addPath="/customer/add"
      editPathBase="/customer/edit/"
      paramKey="identifier"
      deleteStyle="param"
    />
  );
}