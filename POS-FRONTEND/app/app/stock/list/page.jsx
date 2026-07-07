"use client";

import List from "../../../components/List";

export default function StockList() {
  return (
    <List
      title="Stock"
      apiPath="stock"
      addPath="/stock/add"
      editPath="/stock/edit"
      columns={[
        {
          key: "identifier",
          label: "Identifier",
        },
        {
          key: "productName",
          label: "Product",
        },
        {
          key: "warehouseName",
          label: "Warehouse",
        },
        {
          key: "quantity",
          label: "Quantity",
        },
        {
          key: "stockStatus",
          label: "Stock Status",
        },
      ]}
      showStatusToggle={true}
    />
  );
}
