"use client";

import { useState } from "react";
import AddFormSkeleton from "@/components/AddSkeleton";
import { INITIAL_STATE, buildProductFields } from "@/components/dropdowns/productFieldsConfig";

export default function AddProduct() {
  const [fields, setFields] = useState(INITIAL_STATE);

  const handleChange = (key) => (val) => setFields((prev) => ({ ...prev, [key]: val }));

  return (
    <AddFormSkeleton
      title="Product"
      apiPath="product"
      extraFields={buildProductFields(fields, handleChange)}
      extraData={fields}
    />
  );
}