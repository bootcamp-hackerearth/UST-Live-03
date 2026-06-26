"use client";

import CommonList from "@/app/components/CommonList";

export default function CustomerPage() {
  return (
    <CommonList
      title="Customer Management"
      apiUrl="/api/customer/list"
      method="POST"
      deleteApi="/api/customer/delete"
      deleteParam="phoneNo"
      editRoute="/customer/edit/:phoneNo"
      addRoute="/customer/add"
      showStatus={false}

      columns={[
        { header: "ID", field: "id" },
        { header: "Identifier", field: "identifier" },
        { header: "Customer Name", field: "customerName" },
        { header: "Phone", field: "phoneNo" },
        { header: "Party Type", field: "partyType" },
        { header: "Credit Type", field: "creditType" },
        { header: "Credit", field: "credit" },
        { header: "Credit Limit", field: "creditLimit" },
      ]}
    />
  );
}