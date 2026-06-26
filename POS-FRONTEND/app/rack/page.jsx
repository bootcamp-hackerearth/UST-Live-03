"use client";

import React, { useEffect, useState } from "react";

import DynamicList from "../components/common/DynamicList";
import POSLayout from "../components/PosLayout";
import commonApi from "../services/commonApi";

function RackList() {
  const [shelfs, setShelfs] = useState([]);

  useEffect(() => {
    fetchShelfs();
  }, []);

 const fetchShelfs = async () => {
  try {
    const res = await commonApi.active("shelf");
    setShelfs(res.data || []);
  } catch (err) {
    console.log(err);
  }
};

  const columns = [
    {
      key: "identifier",
      label: "Rack",
      type: "text",
    },
    {
      key: "shelf",
      label: "Shelf",
      type: "list",
    },
    {
      key: "status",
      label: "Status",
      type: "toggle",
    },
  ];

  const formFields = [
    {
      key: "identifier",
      label: "Rack Name",
      type: "text",
      placeholder: "Enter Rack Name",
      required: true,
    },
    {
      key: "shelf",
      label: "Shelfs",
      type: "multiselect",
      options: shelfs,
      optionLabel: "identifier",
      optionValue: "identifier",
    },
    {
      key: "status",
      label: "Status",
      type: "select",
      options: [
        {
          label: "Active",
          value: true,
        },
        {
          label: "Inactive",
          value: false,
        },
      ],
      optionLabel: "label",
      optionValue: "value",
    },
  ];

  return (
    <POSLayout>
      <DynamicList
        title="Rack List"
        routeName="rack"
        columns={columns}
        formFields={formFields}
        formTitle="Rack"
        uniqueFields={["identifier"]}
      />
    </POSLayout>
  );
}

export default RackList;