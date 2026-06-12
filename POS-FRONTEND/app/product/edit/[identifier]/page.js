import React from "react";
import EditFormSkeleton from "@/app/components/EditFormSkeleton";

export default function EditProduct() {
  const fields = [
    {
      name: "name",
      label: "Product Name",
      type: "text",
    },
    {
      name: "brand",
      label: "Brand",
      type: "select",
      api: "brand",
      optionLabel: "identifier",
      optionValue: "identifier", 
    },
    {
      name: "unit",
      label: "Unit",
      type: "select",
      api: "unit",
      optionLabel: "identifier",
      optionValue: "identifier",
    },
    {
      name: "model",
      label: "Model",
      type: "select",
      api: "model",
      optionLabel: "identifier",
      optionValue: "identifier",
    },

    
    {
      name: "categories",
      label: "Categories",
      type: "select",
      api: "category",
      multiple: true,
      optionLabel: "identifier",
      optionValue: "identifier",
    },
  ];

  return (
    <EditFormSkeleton
      title="Product"
      apiPath="product"
      fields={fields}
    />
  );
}