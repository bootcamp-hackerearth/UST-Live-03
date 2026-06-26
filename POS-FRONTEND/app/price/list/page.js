"use client";

import ListingSkeleton from "../../components/ListingSkeleton";

const PRICE_FIELDS = ["mrp", "sellingPrice", "costPrice", "effectiveFrom"];

const PRICE_FIELD_LABELS = {
  mrp: "MRP",
  sellingPrice: "Selling Price",
  costPrice: "Cost Price",
  effectiveFrom: "Effective From",
};

const PRICE_APIS = {
  list: "/price/list",
  delete: "/price/delete",
  toggleStatus: "/price/toggle-status",
};

export default function ListPrice() {
  return (
    <ListingSkeleton
      title="Prices"
      fields={PRICE_FIELDS}
      fieldLabels={PRICE_FIELD_LABELS}
      apis={PRICE_APIS}
      addPath="/price/add"
      editPathBase="/price/edit/"
    />
  );
}