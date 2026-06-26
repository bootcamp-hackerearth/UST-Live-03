"use client";

import CommonList from "@/app/components/CommonList";

export default function CustomerListPage() {
  return (
    <CommonList
      title="Customer Management Terminal"

      apiUrl="/api/customer/list"
      method="POST"

      addRoute="/customer/add"
      editRoute="/customer/edit/:phoneNo"

      deleteApi="/api/customer/delete"
      deleteParam="phoneNo"

      // Updated UI data layout tracking columns grid definitions maps
      columns={[
        { header: "ID", field: "id" },
        { header: "Customer Name", field: "customerName" },
        { header: "Phone", field: "phoneNo" },
        { header: "Email Identifier", field: "identifier" }, // Displays the clean target email address
        { header: "Party Type", field: "partyType" },
        { header: "Credit Limit (₹)", field: "creditLimit" },
      ]}

      sortField="id"
      sortOrder="DESC"
      itemsPerPage={10}
      showStatus={false}
    />
  );
}