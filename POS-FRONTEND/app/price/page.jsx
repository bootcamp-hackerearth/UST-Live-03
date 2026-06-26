"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "prop-types";
import { requiredValidation } from "@/validation/validation";

export default function PricesPage() {
  return (
    <CommonList
      routeName="price"
      editField="identifier"
      keys={["identifier", "product", "priceAmount", "priceType"]}
      headers={["Price Code", "Product", "Price Amount", "Price Type"]}
      FormComponent={PriceForm}
    />
  );
}

PriceForm.propTypes = {
  mode: PropTypes.string.isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

function PriceForm({ mode, data, onClose, onSubmit }) {
  const customValidations = {
    product: requiredValidation,
    priceAmount: requiredValidation,
    priceType: requiredValidation,
  };

  return (
    <CommonForm
      title="Price"
      mode={mode}
      data={data}
      onClose={onClose}
      validate={customValidations}
      onSubmit={onSubmit}
      fields={[
        {
          name: "product",
          label: "Product",
          placeholder: "Product",
          type: "select",
          apiUrl: "http://localhost:8080/api/product/list",
          required: true,
        },
        {
          name: "priceAmount",
          label: "Price Amount",
          placeholder: "Price Amount",
          type: "number",
          required: true,
        },
        {
          name: "priceType",
          label: "Price Type",
          placeholder: "Price Type",
          type: "staticSelect",
          options: ["selling price", "cost price", "MRP"],
          required: true,
        },
      ]}
    />
  );
}