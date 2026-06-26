"use client";

import ListingSkeleton from "../../components/ListingSkeleton";

const CATEGORY_FIELDS = ["superCategory"];

const CATEGORY_FIELD_LABELS = {
  superCategory: "Super Category",
};

const CATEGORY_APIS = {
  list: "/category/list",
  delete: "/category/delete",
  toggleStatus: "/category/toggle-status",
};

export default function CategoryListPage() {
  return (
    <ListingSkeleton
      title="Category"
      fields={CATEGORY_FIELDS}
      fieldLabels={CATEGORY_FIELD_LABELS}
      apis={CATEGORY_APIS}
      addPath="/category/add"
      editPathBase="/category/edit/"
    />
  );
}