import React from "react";
import AddFormSkeleton from "@/app/components/AddFormSkeleton";

export default function AddProduct() {
  const fields = [
    {
      name: "name",
      label: "Product Name",
      type: "text",
      required: true,
    },
    {
      name: "brand",
      label: "Brand",
      type: "select",
      api: "brand",
      multiple: false,
      optionLabel: "identifier",
      optionValue: "identifier",
    },
    {
      name: "unit",
      label: "Unit",
      type: "select",
      api: "unit",
      multiple: false,
      optionLabel: "identifier",
      optionValue: "identifier",
    },
    {
      name: "model",
      label: "Model",
      type: "select",
      api: "model", 
      multiple: false,
      optionLabel: "identifier",
      optionValue: "identifier",
    },
    {
      name: "categories",
      label: "Category",
      type: "select",
      api: "category",
      multiple: true,  
      optionLabel: "identifier",
      optionValue: "identifier",
    },
  ];

  return (
    <AddFormSkeleton
      title="Product"
      apiPath="product"
      fields={fields}
    />
  );
}