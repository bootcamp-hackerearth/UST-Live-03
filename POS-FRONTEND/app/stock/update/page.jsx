'use client';

import UpdatePage from "../../../components/common/UpdatePage";
import Sidebar from "../../../components/layout/Sidebar";

import Product from "../../../components/dropdown/Product";
import Warehouse from "../../../components/dropdown/Warehouse";
import Racks from "../../../components/dropdown/Racks";
import Shelves from "../../../components/dropdown/shelves";

export default function StockUpdate() {
  const fields = [
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
      <UpdatePage
        fields={fields}
        modelName="stock"
      />
    </Sidebar>
  );
}