"use client";
import React from "react";
import CommonList from "@/app/components/CommonList" 

const ShelfList = () => {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Shelf", field: "identifier" },
    { label: "Description", field: "description" },
    { label: "Status", field: "status" },
 
  ];

  return (
    <CommonList
      title="Shelf Management"
      columns={columns}
      urlName="shelf"   
      showStatus={true}
    />
  );
};

export default ShelfList;