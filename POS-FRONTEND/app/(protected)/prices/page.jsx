"use client";

import CommonList from "@/components/table/CommonList";

export default function Prices() {
  const columns = [
    {
      header: "Identifier",
      field: "identifier",
    },
    {
      header: "Product",
      field: "product",
    },
    {
      header: "Price Type",
      field: "priceType",
    },
    {
      header: "Cost Price",
      field: "costPrice",
    },
  ];

  return (
    <CommonList
      title="Prices"
      subtitle="Manage product pricing"
      entity="price"
      addPath="/prices/add"
      editPath="/prices/edit"
      columns={columns}
      showToggle={false}
    />
  );
}