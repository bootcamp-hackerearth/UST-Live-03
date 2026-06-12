"use client";
import React from "react";
import CommonList from "@/app/components/CommonList" 

const RoleList = () => {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Role", field: "identifier" },
    { label: "Description", field: "description" },
    { label: "Status", field: "status" },
  ];

  return (
    <CommonList
      title="Role Management"
      columns={columns}
      urlName="role"   
      showStatus={true}
    />
  );
};

export default RoleList;