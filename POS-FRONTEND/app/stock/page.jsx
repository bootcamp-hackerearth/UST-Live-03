'use client';

import ListPage from "../../components/Common/ListPage";
import Sidebar from "../../components/layout/Sidebar";

import Product from "../../components/dropdown/Product";
import Warehouse from "../../components/dropdown/Warehouse";
import Racks from "../../components/dropdown/Racks";
import Shelves from "../../components/dropdown/shelves";

export default function StockList() {
  const keys = [
    "identifier",
    "product",
    "wareHouse",
    "quantity",
    "racks",
    "shelves",
  ];

  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
    },
    {
      name: "product",
      label: "Product",
      component: Product,
    },
    {
      name: "wareHouse",
      label: "Warehouse",
      component: Warehouse,
    },
    {
      name: "quantity",
      label: "Quantity",
      type: "number",
    },
    {
      name: "racks",
      label: "Rack",
      component: Racks,
    },
    {
      name: "shelves",
      label: "Shelf",
      component: Shelves,
    },
  ];

  return (
    <Sidebar>
      <ListPage
        keys={keys}
        fields={fields}
        modelName="stock"
      />
    </Sidebar>
  );
}