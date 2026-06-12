"use client";

import ListingSkeleton from "@/components/ListingSkeleton";

const PRICE_APIS = {
  list:         "/price/list",
  delete:       "/price/delete",
  toggleStatus: "/price/toggle-status",
};

const PRICE_FIELDS = ["mrp", "sellingPrice", "costPrice", "effectiveFrom"];

export default function ListPrice() {
  return (
    <ListingSkeleton
      title="Prices"
      fields={PRICE_FIELDS}
      apis={PRICE_APIS}
      addPath="/price/add"
      editPathBase="/price/edit/"
      paramKey="identifier"
      identifierLabel="Product Identifier"
      deleteStyle="param"
    />
  );
}