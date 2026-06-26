"use client";

import React from "react";
import ListPage from "../components/common/ListPage";

const columns = [
  { key: "identifier", label: "Identifier", type: "text" },
  { key: "name", label: "Warehouse Name", type: "text" },
  { key: "address", label: "Address", type: "text" },
  { key: "region", label: "Region", type: "text" },
  { key: "country", label: "Country", type: "text" },
  { key: "phoneNo", label: "Phone Number", type: "text" },
];

function WarehouseList() {
  return (
    <ListPage
      title="Warehouse List"
      routeName="warehouse"
      columns={columns}
      editUrl="/warehouse/edit"
      addUrl="/warehouse/add"
    />
  );
}

export default WarehouseList;