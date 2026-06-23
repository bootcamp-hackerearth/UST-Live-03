"use client";

import React from "react";
import CommonList from "@/app/components/CommonList"; 

const WareHouseList = () => {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Identifier", field: "identifier" },
    { label: "Manager", field: "manager" },
    {label: "Location",field: "location"},
    { label: "Status", field: "status" }, 
  ];

  return (
    <CommonList
      title="WareHouse Management"
      columns={columns}
      urlName="warehouse"
      showStatus={true}
      editKey="identifier"
    />
  );
};

export default WareHouseList;