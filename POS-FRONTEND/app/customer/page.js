"use client";

import React from "react";
import CommonList from "@/app/components/CommonList";

const CustomerList = () => {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Code", field: "identifier" },
    { label: "Customer Name", field: "customerName" },
    { label: "Phone No", field: "phoneNo" },
    { label: "Party Type", field: "partyType" },
    { label: "Credit Type", field: "creditType" },
    { 
      label: "Credit", 
      render: (item) => item.credit == null ? "$0.00" : `$${Number.parseFloat(item.credit).toFixed(2)}` 
    },
    { 
      label: "Credit Limit", 
      render: (item) => item.creditLimit == null ? "$0.00" : `$${Number.parseFloat(item.creditLimit).toFixed(2)}` 
    },
    { label: "Status", field: "status" },
  ];

  return (
    <CommonList
      title="Customer Management"
      columns={columns}
      urlName="customer"
      showStatus={true}
    />
  );
};

export default CustomerList;