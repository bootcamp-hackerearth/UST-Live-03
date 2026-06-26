"use client";
import React from "react";
import CommonAdd from "../../Components/CommonAdd";
import Layout from "@/app/Components/Layout";
import { warehouseFields } from "../../Components/FormSchemas";

export default function AddWarehouse() {
  return (
    <Layout>
      <CommonAdd 
      title="Warehouse" 
      apiPath="warehouse" 
      extraFields={warehouseFields} 
      onSuccessPath="/warehouse/list" 
      />
    </Layout>
  );
}