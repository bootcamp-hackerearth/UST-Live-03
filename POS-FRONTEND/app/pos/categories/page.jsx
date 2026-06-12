// app/pos/categories/page.jsx

"use client";

import BaseListForm from "../../../components/lists/BaseListForm";

export default function CategoryListPage() {
  const columns = [
    { key: "id", label: "ID" },
    { key: "identifier", label: "Category Name" },
    { key: "superCategory", label: "Super Category" },
    { key: "status", label: "Status" },
  ];

  return (
    <BaseListForm
      title="Categories"
      entity="category"
      columns={columns}
      addPath="/pos/categories/add"
      editPath="/pos/categories/edit"
      identifierKey="identifier"
    />
  );
}