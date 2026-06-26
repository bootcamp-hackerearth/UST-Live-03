'use client';
import AddPage from "../../../components/Common/AddPage";

import Product from "../../../components/dropdown/Product";
import Warehouse from "../../../components/dropdown/Warehouse";
import Racks from "../../../components/dropdown/Racks";
import Shelves from "../../../components/dropdown/shelves";

export default function StockAdd() {
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
      <AddPage
        modelName="stock"
        fields={fields}
        initialData={{
          identifier: "",
          product: "",
          wareHouse: "",
          quantity: "",
          racks: "",
          shelves: "",
        }}
        renderForm={({ formData }) => {
          if (
            formData.product &&
            formData.wareHouse
          ) {
            formData.identifier =
              `${formData.product}_${formData.wareHouse}`;
          }

          return null;
        }}
      />
  );
}