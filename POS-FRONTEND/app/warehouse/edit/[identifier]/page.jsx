"use client";

import React from "react";
import EditPage from "../../../components/common/EditPage";

const sections = [
  {
    title: "Basic Information",
    columns: 3,
    fields: [
      {
        key: "name",
        label: "Warehouse Name",
        type: "text",
        required: true,
      },
      {
        key: "identifier",
        label: "Warehouse Identifier",
        type: "text",
        required: true,
        disabled: true,
      },
      {
        key: "phoneNo",
        label: "Phone Number",
        type: "text",
        required: true,
        isPhone: true,
      },
    ],
  },
  {
    title: "Address Information",
    columns: 2,
    fields: [
      {
        key: "address",
        label: "Address",
        type: "text",
        required: true,
        fullWidth: true,
      },
      {
        key: "region",
        label: "Region",
        type: "text",
        required: true,
      },
      {
        key: "country",
        label: "Country",
        type: "text",
        required: true,
      },
    ],
  },
];

function WarehouseEdit() {
  return (
    <EditPage
      title="Edit Warehouse"
      routeName="warehouse"
      backUrl="/warehouse"
      sections={sections}
    />
  );
}

export default WarehouseEdit;