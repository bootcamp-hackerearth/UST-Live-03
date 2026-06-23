"use client";
import React from "react";
import CommonList from "@/app/components/CommonList";

const BrandList = () => {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Brand", field: "identifier" },
    { label: "Description", field: "description" },
    { label: "Status", field: "status" },
  ];

  return (
    <CommonList
      title="Brand Management"
      columns={columns}
      urlName="brand"
      showStatus={true}
    />
  );
};

export default BrandList;
