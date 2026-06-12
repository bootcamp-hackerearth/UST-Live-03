// app/pos/products/add/page.jsx

"use client";
import React from "react";
import BaseAddForm from "../../../../components/add/BaseAddForm";
import { useProductFields } from "../../../../components/useProductFields";

export default function ProductAdd() {
  const { productFields, consolidatedDropdownData } = useProductFields();

  return (
    <BaseAddForm
      title="Product"
      apiPath="product"
      extraFields={productFields}
      extraData={consolidatedDropdownData}
    />
  );
}