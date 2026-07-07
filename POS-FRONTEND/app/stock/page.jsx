"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "prop-types";
import { requiredValidation } from "@/validation/validation";

export default function StockPage() {
  return (
    <CommonList
      routeName="stock"
      editField="identifier"
      keys={["identifier", "product", "warehouse", "quantity"]}
      headers={["Stock Code", "Product", "Warehouse", "Quantity"]}
      FormComponent={StockForm}
    />
  );
}

StockForm.propTypes = {
  mode: PropTypes.string.isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

function StockForm({ mode, data, onClose, onSubmit }) {
  const customValidations = {
    identifier: requiredValidation,
    product: requiredValidation,
    warehouse: requiredValidation,
    quantity: requiredValidation,
  };

  return (
    <CommonForm
      title="Stock"
      mode={mode}
      data={data}
      onClose={onClose}
      validate={customValidations}
      onSubmit={async (formData) => {
        formData.quantity = Number(formData.quantity || 0);
        await onSubmit(formData);
      }}
      fields={[
        {
          name: "identifier",
          label: "Stock Code",
          placeholder: "Stock Code",
          required: true,
        },
        {
          name: "product",
          label: "Product",
          placeholder: "Product",
          type: "select",
          apiUrl: `${process.env.NEXT_PUBLIC_BASE_URL}/api/product/list`,
          required: true,
        },
        {
          name: "warehouse",
          label: "Warehouse",
          placeholder: "Warehouse",
          type: "select",
          apiUrl: `${process.env.NEXT_PUBLIC_BASE_URL}/api/warehouse/list`,
          required: true,
        },
        {
          name: "quantity",
          label: "Quantity",
          placeholder: "Quantity",
          type: "number",
          required: true,
        },
      ]}
    />
  );
}