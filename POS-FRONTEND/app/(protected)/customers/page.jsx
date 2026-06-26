"use client";

import CommonList from "@/components/table/CommonList";

const customerColumns = [
  { header: "Email", field: "identifier" },
  { header: "Name", field: "name" },
  { header: "Phone", field: "phoneNo" },
  { header: "Party Type", field: "partyType" },
  { header: "Balance", field: "balance" },
  { header: "Credit Limit", field: "creditLimit" },
];

export default function CustomersPage() {
  return (
    <CommonList
      title="Customers"
      subtitle="Manage your customers"
      entity="customer"
      addPath="/customers/add"
      editPath="/customers/edit"
      columns={customerColumns}
      showToggle={false}
    />
  );
}