"use client";

import ListingSkeleton from "../../components/ListingSkeleton";

export default function ListPrice() {
  return (
    <ListingSkeleton
      title="Prices"
      fields={[
        "mrp",
        "sellingPrice",
        "costPrice",
        "effectiveFrom",
      ]}
      apis={{
        list: "/price/list",
        delete: "/price/delete",
        toggleStatus: "/price/toggle-status",
      }}
      addPath="/price/add"
      editPathBase="/price/edit/"
    />
  );
}