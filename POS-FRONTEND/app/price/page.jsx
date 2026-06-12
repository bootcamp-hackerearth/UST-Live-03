'use client';

import Sidebar from "../../components/layout/Sidebar";
import ListPage from "../../components/common/ListPage";

export default function PriceList() {

  const keys = [
    "identifier",
    "product",
    "priceType",
    "amount"
  ];

  const fields = [
    {
      name: "product",
      label: "Product",
      type: "text"
    },
    {
      name: "priceType",
      label: "Price Type",
      type: "text"
    },
    {
      name: "amount",
      label: "Amount",
      type: "number"
    }
  ];

  return (
    <Sidebar>
    <ListPage
      keys={keys}
      fields={fields}
      modelName="price"
      showToggle={false}
    />
    </Sidebar>
  );
}