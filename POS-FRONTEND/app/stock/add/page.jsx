"use client";
 
import React from "react";
import Layout from "../../Components/Layout";
import CommonAdd from "../../Components/CommonAdd";
 
export default function AddStock() {
  const extraFields = [
    {
      key: "warehouse",
      label: "Warehouse Location",
      type: "select",
      required: true,
      apiPath: "warehouse",
      optionLabel: "location",
      optionValue: "location",
      placeholder: "Assign Warehouse...",
    },
    {
      key: "quantity",
      label: "Quantity",
      type: "number",
      required: true,
      placeholder: "Enter quantity",
      min: 1,
    },
    {
      key: "unit",
      label: "Unit of Measure",
      type: "select",
      required: true,
      apiPath: "unit",
      optionLabel: "identifier",
      optionValue: "identifier",
      placeholder: "Select Unit",
    },
    {
      key: "stockStatus",
      label: "Status",
      type: "select",
      required: true,
      options: [
        {
          label: "ACTIVE",
          value: "ACTIVE",
        },
        {
          label: "INACTIVE",
          value: "INACTIVE",
        },
      ],
    },
  ];
 
  return (
    <Layout>
      <CommonAdd
        title="Stock"
        apiPath="stock"
        extraFields={extraFields}
        onSuccessPath="/stock/list"
      />
    </Layout>
  );
}