"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "prop-types";
import { requiredValidation } from "@/validation/validation";

export default function ProductsPage() {
  return (
    <CommonList
      routeName="product"
      editField="identifier"
      keys={["identifier", "name", "unit", "category", "brand"]}
      headers={["UID", "Product Name", "Unit", "Category", "Brand"]}
      FormComponent={ProductForm}
    />
  );
}

ProductForm.propTypes = {
  mode: PropTypes.string.isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

function ProductForm({ mode, data, onClose, onSubmit }) {
  const customValidations = {
    identifier: requiredValidation,
    name: requiredValidation,
    unit: requiredValidation,
    category: requiredValidation,
    brand: requiredValidation,
  };

  return (
    <CommonForm
      title="Product"
      mode={mode}
      data={data}
      onClose={onClose}
      validate={customValidations}
      onSubmit={onSubmit}
      fields={[
        {
          name: "identifier",
          label: "UID",
          placeholder: "UID",
          required: true,
        },
        {
          name: "name",
          label: "Product Name",
          placeholder: "Product Name",
          required: true,
        },
        {
          name: "unit",
          label: "Unit",
          placeholder: "Unit",
          required: true,
        },
        {
          name: "category",
          label: "Category",
          placeholder: "Category",
          type: "select",
          apiUrl: `${process.env.NEXT_PUBLIC_BASE_URL}/category/list`,
          required: true,
        },
        {
          name: "brand",
          label: "Brand",
          placeholder: "Brand",
          type: "select",
          apiUrl: `${process.env.NEXT_PUBLIC_BASE_URL}/brand/list`,
          required: true,
        },
      ]}
    />
  );
}