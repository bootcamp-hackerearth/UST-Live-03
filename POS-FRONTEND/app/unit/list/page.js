"use client";
import React from "react";
import CommonList from "@/app/components/CommonList";

const UnitList = () => {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Unit", field: "identifier" },
    { label: "Description", field: "description" },
    { label: "Status", field: "status" },
  ];

  return (
    <CommonList
      title="Unit Management"
      columns={columns}
      urlName="unit"
      showStatus={true}
    />
  );
};

export default UnitList;
