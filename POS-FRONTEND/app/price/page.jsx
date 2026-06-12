"use client";

import React from "react";

import DynamicList from "../components/common/DynamicList";
import POSLayout from "../components/PosLayout";

function PriceList() {
  const columns = [
    {
      key: "identifier",  
      label: "Price Identifier",
      type: "text",
    },
    {
      key: "productIdentifier",
      label: "Product Identifier",
      type: "text",
    },
    {
      key: "priceType",
      label: "Price Type",
      type: "text",
    },
    {
      key: "priceAmount",
      label: "Price Amount",
      type: "text",
    },
  ];

  return (
    <POSLayout>
      <DynamicList
        title="Price List"
        routeName="price"
        columns={columns}
        editUrl="/price/edit"
        addUrl="/price/add"
      />
    </POSLayout>
  );
}

export default PriceList;