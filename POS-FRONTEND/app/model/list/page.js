"use client";
import React from "react";
import CommonList from "@/app/components/CommonList";

const ModelList = () => {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Brand", field: "identifier" },
    { label: "Description", field: "description" },
    { label: "Status", field: "status" },
  ];

  return (
    <CommonList
      title="Model Management"
      columns={columns}
      urlName="model"
      showStatus={true}
    />
  );
};

export default ModelList;
