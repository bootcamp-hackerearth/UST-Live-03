"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "prop-types";
import { requiredValidation } from "@/validation/validation"


export default function CategoriesPage() {
  return (
    <CommonList
      routeName="category"
      editField="identifier"
      keys={["identifier", "superCategory"]}
      headers={["Category Name", "Super Category"]}
      FormComponent={CategoryForm}
    />
  );
}

CategoryForm.propTypes = {
  mode: PropTypes.string.isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

function CategoryForm({ mode, data, onClose, onSubmit }) {
  return (
    <CommonForm
      title="Category"
      mode={mode}
      data={data}
      onClose={onClose}
      onSubmit={onSubmit}
      fields={[
        {
          name: "identifier",
          label: "Category Name",
          placeholder: "Category Name",
          required: true,
          validation: requiredValidation,
        },
        {
          name: "superCategory",
          label: "Super Category",
          placeholder: "Super Category",
          validation: requiredValidation,
          type: "multiselect",
          apiUrl: `${process.env.NEXT_PUBLIC_BASE_URL}/api/category/list`,
          required: false,
        },
      ]}
    />
  );
}