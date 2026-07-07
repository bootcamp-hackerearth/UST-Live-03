"use client";

import PropTypes from "prop-types";
import CommonForm from "@/components/CommonForm";
import CommonList from "@/components/CommonList";

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
    apiUrl: process.env.NEXT_PUBLIC_BASE_URL+"/product/list",
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

  const isEdit = !!(props.data || props.initialData);

  const filteredFields = isEdit
    ? priceFields
    : priceFields.filter((field) => field.name !== "identifier");

  return (
    <CommonForm
      {...rest}
      title="Price"
      fields={filteredFields}
      validate={priceValidate}
    />
  );
};

PriceForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  data: PropTypes.shape({
    identifier: PropTypes.string,
    product: PropTypes.string,
    priceAmount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    priceType: PropTypes.string,
  }),
  initialData: PropTypes.shape({
    identifier: PropTypes.string,
    product: PropTypes.string,
    priceAmount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    priceType: PropTypes.string,
  }),
  fields: PropTypes.arrayOf(PropTypes.object),
  mode: PropTypes.string,
  onClose: PropTypes.func,
  validate: PropTypes.func,
};

PriceForm.defaultProps = {
  data: null,
  initialData: null,
  fields: [],
  mode: "add",
  onClose: () => {},
  validate: null,
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
