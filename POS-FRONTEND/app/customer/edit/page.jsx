"use client";
import React from "react";
import CommonEdit from "../../Components/CommonEdit";
import Layout from "@/app/Components/Layout";
import { customerFields } from "../../Components/FormSchemas";

export default function EditCustomer() {
  return (
    <Layout>
      <CommonEdit 
      title="Customer" 
      apiPath="customer" 
      extraFields={customerFields} 
      onSuccessPath="/customer/list" 
      />
    </Layout>
  );
}