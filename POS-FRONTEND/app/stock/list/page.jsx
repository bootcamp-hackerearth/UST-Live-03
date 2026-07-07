"use client";

import CommonListPage from "@/components/common/CommonListPage";

const StockListPage = () => {
  return (
    <CommonListPage
      modelName="stock"
      keys={[
        "identifier",
        "productIdentifier",
        "warehouseIdentifier",
        "availableQuantity",
        "reorderLevel",
        "stockState",
      ]}
      enableToggle={true}
    />
  );
};

export default StockListPage;