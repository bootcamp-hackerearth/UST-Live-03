"use client";

import ListingSkeleton from "@/components/ListingSkeleton";

export default function ListCategory() {
  return (
    <ListingSkeleton
      title="Categories"
      fields={["superCategory"]}
      apis={{
        list: "/category/list",
        delete: "/category/delete",
        toggleStatus: "/category/toggle-status",
      }}
      addPath="/category/add"
      editPathBase="/category/edit/"
      paramKey="identifier"
      deleteStyle="param"
      identifierLabel="Category Name"
    />
  );
}