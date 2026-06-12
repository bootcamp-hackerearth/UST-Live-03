"use client";

import { useState } from "react";
import EditFormSkeleton from "@/components/EditSkeleton";
import { INITIAL_STATE, buildProductFields } from "@/components/dropdowns/productFieldsConfig";

export default function EditProduct() {
  const [fields, setFields] = useState(INITIAL_STATE);

  const handleChange = (key) => (val) => setFields((prev) => ({ ...prev, [key]: val }));

  return (
    <EditFormSkeleton
      title="Product"
      apiPath="product"
      extraFields={buildProductFields(fields, handleChange)}
      extraData={fields}
      setters={{
        brand:    handleChange("brand"),
        unit:     handleChange("unit"),
        model:    handleChange("model"),
        category: handleChange("category"),
      }}
    />
  );
}