"use client";

import List from "@/app/components/CommonList";

export default function CustomerListPage() {
  return (
    <List
      urlName="customer"
      keys={[
        "name",
        "identifier",
        "phoneNo",
        "partyType",
        "balance",
        "creditLimit",
      ]}
    />
  );
}