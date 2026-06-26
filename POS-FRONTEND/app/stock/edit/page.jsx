"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonEdit from "../../Components/CommonEdit";
 
export default function EditStock() {
  const extraFields = [
   
    {
      key: "warehouse",
      label: "Warehouse Location",
      type: "select",
      required: true,
      optionLabel: "location",
      optionValue: "location",
      apiPath: "warehouse",
    },
    {
      key: "quantity",
      label: "Quantity",
      type: "number",
      placeholder: "Enter quantity",
      required: true,
    },
    {
      key: "unit",
      label: "Unit of Measure",
      type: "select",
      required: true,
      optionLabel: "identifier",
      optionValue: "identifier",
      apiPath: "unit",
    },
    {
      key: "status",
      label: "Status",
      type: "select",
      valueType: "boolean",
      required: true,
      options: [
        {
          label: "Active",
          value: "true",
        },
        {
          label: "Inactive",
          value: "false",
        },
      ],
    },
  ];
 
  return (
    <Layout>
      <CommonEdit
        title="Stock"
        apiPath="stock"
        extraFields={extraFields}
        onSuccessPath="/stock/list"
          />
    </Layout>
  );
}