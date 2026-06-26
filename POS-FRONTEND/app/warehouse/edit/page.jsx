"use client";
import React from "react";
import CommonEdit from "../../Components/CommonEdit";
import Layout from "@/app/Components/Layout";
import { warehouseFields } from "../../Components/FormSchemas";

export default function EditWarehouse() {
  return (
    <Layout>
      <CommonEdit title="Warehouse" apiPath="warehouse" extraFields={warehouseFields} onSuccessPath="/warehouse/list" />
    </Layout>
  );
}