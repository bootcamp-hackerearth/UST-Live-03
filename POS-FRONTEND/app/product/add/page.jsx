"use client";
import React from "react";
import CommonAdd from "../../Components/CommonAdd";
import Layout from "@/app/Components/Layout";
import { productFields } from "../../Components/FormSchemas";

export default function AddProduct() {
  return (
    <Layout>
      <CommonAdd title="Product" apiPath="product" extraFields={productFields} onSuccessPath="/product/list" />
    </Layout>
  );
}