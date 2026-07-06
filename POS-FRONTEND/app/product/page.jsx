"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";

const productFields = [
  { name: "identifier", placeholder: "Identifier" },
  { name: "name", placeholder: "Name" },
  {
    name: "unit",
    placeholder: "Unit",
    type: "select",
    apiUrl: "/api/unit/list",
  },
  {
    name: "brand",
    placeholder: "Brand",
    type: "select",
    apiUrl: "/api/brand/list",
  },
  {
    name: "category",
    placeholder: "Category",
    type: "select",
    apiUrl: "/api/category/list",
  },
  {
    name: "models",
    placeholder: "Models",
    type: "select",
    apiUrl: "/api/models/list",
  },
];

const productValidate = (formData) => {
  const errors = {};

  if (!formData.identifier?.trim()) {
    errors.identifier = "ID is required";
  }

  if (!formData.name?.trim()) {
    errors.name = "Name is required";
  }

  if (!formData.unit) {
    errors.unit = "Unit is required";
  }

  if (!formData.brand) {
    errors.brand = "Brand is required";
  }

  if (!formData.category) {
    errors.category = "Category is required";
  }

  if (!formData.models) {
    errors.models = "Model is required";
  }

  return errors;
};

const ProductForm = (props) => {
  const rest = { ...props };
  delete rest.fields;
  return (
    <CommonForm {...rest} fields={productFields} title="Product" validate={productValidate} />
  );
};

export default function ProductPage() {

  const keys = [
    "identifier",
    "name",
    "unit",
    "brand",
    "category",
    "models",
  ];

  return (
    <CommonList
      keys={keys}
      routeName="product"
      editField="identifier"
      FormComponent={ProductForm}
    />
  );
}