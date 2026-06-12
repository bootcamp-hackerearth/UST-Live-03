// app/pos/products/edit/[identifier]/page.jsx

"use client";
import React from "react";
import BaseEditForm from "../../../../../components/edit/BaseEditForm";
import { useProductFields } from "../../../../../components/useProductFields";

export default function ProductEdit() {
  const { productFields, consolidatedDropdownData, functionalSetters } = useProductFields();

  return (
    <BaseEditForm
      title="Product"
      apiPath="product"
      extraFields={productFields}
      setters={functionalSetters}
      extraData={consolidatedDropdownData}
    />
  );
}