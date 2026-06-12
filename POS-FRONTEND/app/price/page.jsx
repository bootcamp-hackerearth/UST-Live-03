"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";

const priceFields = [
  {
    name: "identifier",
    placeholder: "Identifier",
    disableOnEdit: true,
  },
  {
    name: "product",
    placeholder: "Product",
    type: "select",
    apiUrl: "http://localhost:8080/api/product/list",
    disableOnEdit: true,
  },
  {
    name: "priceAmount",
    placeholder: "Price Amount",
    type: "text",
  },
  {
    name: "priceType",
    placeholder: "Price Type",
    type: "select",
    disableOnEdit: true,
    options: [
      { value: "COST", label: "Cost Price" },
      { value: "SELLING", label: "Selling Price" },
      { value: "MRP", label: "MRP" },
    ],
  },
];

const priceValidate = (formData) => {
  const errors = {};

  if (!formData.identifier?.trim()) {
    errors.identifier = "Identifier is required";
  }

  if (!formData.product) {
    errors.product = "Product is required";
  }

  if (!formData.priceAmount?.toString().trim()) {
    errors.priceAmount = "Price amount is required";
  } else if (Number(formData.priceAmount) <= 0) {
    errors.priceAmount = "Must be greater than 0";
  }

  if (!formData.priceType) {
    errors.priceType = "Price type is required";
  }

  return errors;
};

const PriceForm = (props) => {
  const rest = { ...props };
  delete rest.fields;
  return (
    <CommonForm {...rest} title="Price" fields={priceFields} validate={priceValidate} />
  );
};

export default function PricePage() {
  const keys = ["identifier", "product", "priceAmount", "priceType"];

  return (
    <CommonList
      routeName="price"
      keys={keys}
      editField="identifier"
      FormComponent={PriceForm}
    />
  );
}