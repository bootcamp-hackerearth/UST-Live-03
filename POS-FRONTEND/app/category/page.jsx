"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";

const categoryFields = [
  {
    name: "identifier",
    placeholder: "Category Name",
  },
  {
    name: "superCategory",
    placeholder: "Super Category",
    type: "multiselect",
    apiUrl: "/api/category/list",
  },
];

const categoryValidate = (formData) => {
  const errors = {};

  if (!formData.identifier?.trim()) {
    errors.identifier = "Category name is required";
  }

  if (!formData.superCategory || formData.superCategory.length === 0) {
    errors.superCategory = "Select at least one super category";
  }

  return errors;
};

const CategoryForm = (props) => {
  const rest = { ...props };
  delete rest.fields;
  return (
    <CommonForm
      {...rest}
      fields={categoryFields}
      title="Category"
      validate={categoryValidate}
    />
  );
};

export default function CategoryPage() {
  const keys = ["identifier", "superCategory"];

  return (
    <CommonList
      keys={keys}
      routeName="category"
      editField="identifier"
      FormComponent={CategoryForm}
    />
  );
}