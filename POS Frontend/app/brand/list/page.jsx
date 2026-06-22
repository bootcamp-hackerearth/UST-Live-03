"use client";

import ListingSkeleton from "@/components/CommonListForm";

export default function ListBrand() {
  return (
    <ListingSkeleton
      title="Brands"
      fields={["description"]}
      apis={{
        list: "/brand/list",
        delete: "/brand/delete",
        toggleStatus: "/brand/toggle-status",
      }}
      addPath="/brand/add"
      editPathBase="/brand/edit/"
      paramKey="identifier"
      deleteStyle="param"
    />
  );
}
