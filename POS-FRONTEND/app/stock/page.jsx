"use client";

import PropTypes from "prop-types";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";

const stockFields = [
  {
    name: "identifier",
    placeholder: "Identifier",
    disableOnEdit: true,
  },
  {
    name: "product",
    placeholder: "Product",
    type: "select",
    apiUrl: "/api/product/list",
  },
  {
    name: "quantity",
    placeholder: "Quantity",
    type: "text",
  },
  {
    name: "status",
    placeholder: "Status",
    type: "select",
    options: [
      { value: "AVAILABLE", label: "Available" },
      { value: "OUT_OF_STOCK", label: "Out of Stock" },
      { value: "DAMAGED", label: "Damaged" },
    ],
  },
  {
    name: "warehouse",
    placeholder: "Warehouse",
    type: "select",
    apiUrl: "http://localhost:8080/api/warehouse/list",
  },
];

const stockValidate = (formData) => {
  const errors = {};

  if (!formData.product) {
    errors.product = "Product is required";
  }

 const qty = Number(formData.quantity);

  if (!formData.quantity?.toString().trim()) {
    errors.quantity = "Quantity is required";
  } else if (Number.isNaN(qty) || !Number.isInteger(qty) || qty < 0) {
    errors.quantity = "Quantity must be a whole number greater than or equal to0";
  }

  if (!formData.status) {
    errors.status = "Status is required";
  }

  if (!formData.warehouse) {
    errors.warehouse = "Warehouse is required";
  }

  return errors;
};

const StockForm = (props) => {
  const rest = { ...props };
  delete rest.fields;

  const isEdit =
    props.initialData ||
    props.data ||
    props.formData;

  const filteredFields = isEdit
    ? stockFields
    : stockFields.filter((field) => field.name !== "identifier");

  return (
    <CommonForm
      {...rest}
      title="Stock"
      fields={filteredFields}
      validate={stockValidate}
    />
  );
};

StockForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  data: PropTypes.shape({
    identifier: PropTypes.string,
    product: PropTypes.string,
    quantity: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    status: PropTypes.string,
    warehouse: PropTypes.string,
  }),
  initialData: PropTypes.shape({
    identifier: PropTypes.string,
    product: PropTypes.string,
    quantity: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    status: PropTypes.string,
    warehouse: PropTypes.string,
  }),
  formData: PropTypes.shape({
    identifier: PropTypes.string,
    product: PropTypes.string,
    quantity: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    status: PropTypes.string,
    warehouse: PropTypes.string,
  }),
  fields: PropTypes.arrayOf(PropTypes.object),
  mode: PropTypes.string,
  onClose: PropTypes.func,
  validate: PropTypes.func,
};

StockForm.defaultProps = {
  data: null,
  initialData: null,
  formData: null,
  fields: [],
  mode: "add",
  onClose: () => {},
  validate: null,
};
export default function StockPage() {
  const keys = [
    "identifier",
    "product",
    "quantity",
    "status",
    "warehouse",
  ];

  return (
    <CommonList
      routeName="stock"
      keys={keys}
      editField="identifier"
      FormComponent={StockForm}
    />
  );
}