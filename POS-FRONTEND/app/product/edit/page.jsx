"use client";
import React from "react";
import CommonEdit from "../../Components/CommonEdit";
import Layout from "@/app/Components/Layout";
import { productFields } from "../../Components/FormSchemas";

export default function EditProduct() {
  return (
    <Layout>
      <CommonEdit title="Product" apiPath="product" extraFields={productFields} onSuccessPath="/product/list" />
    </Layout>
  );
}