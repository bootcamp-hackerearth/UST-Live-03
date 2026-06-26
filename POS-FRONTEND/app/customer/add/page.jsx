"use client";
import React from "react";
import CommonAdd from "../../Components/CommonAdd";
import Layout from "@/app/Components/Layout";
import { customerFields } from "../../Components/FormSchemas";

export default function AddCustomer() {
  return (
    <Layout>
      <CommonAdd 
      title="Customer" 
      apiPath="customer" 
      extraFields={customerFields} 
      onSuccessPath="/customer/list" 
      />
    </Layout>
  );
}