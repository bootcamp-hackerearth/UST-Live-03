"use client";

import List from "../../../components/List";

export default function StockList() {
  return (
    <List
      title="STOCK"
      apiPath="stock"
      addPath="/stock/add"
      editPath="/stock/edit"
      columns={[
        {
          key: "identifier",
          label: "IDENTIFIER",
        },
        {
          key: "productName",
          label: "PRODUCT",
        },
        {
          key: "warehouseName",
          label: "WAREHOUSE",
        },
        {
          key: "quantity",
          label: "QUANTITY",
        },
        {
          key: "stockStatus",
          label: "STOCK STATUS",
        },
      ]}
      showStatusToggle={true}
    />
  );
}
