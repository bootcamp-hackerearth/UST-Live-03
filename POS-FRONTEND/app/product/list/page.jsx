"use client";

import ListingSkeleton from "@/components/ListingSkeleton";

export default function ListProduct() {
  return (
    <ListingSkeleton
      title="Products"
      fields={[
        "name",
        "brand",
        "model",
        "unit",
        "category",
      ]}
      apis={{
        list: "/product/list",
        delete: "/product/delete",
        toggleStatus: "/product/toggle-status",
      }}
      addPath="/product/add"
      editPathBase="/product/edit/"
    />
  );
}