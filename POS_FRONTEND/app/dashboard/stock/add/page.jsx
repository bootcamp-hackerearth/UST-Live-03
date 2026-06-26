"use client";

import CommonForm from "@/components/CommonForm";

export default function AddStockPage() {

  const fields = [
    {
      name: "product",
      label: "Product",
      type: "select",
      api: "/product/list",
      required: true
    },
    {
      name: "quantity",
      label: "Quantity",
      type: "text",
      required: true
    },

    {
      name: "warehouse",
      label: "Warehouse",
      type: "select",
      api: "/warehouse/list",
      required: true
    },
  ];

  return (
    <div className="min-h-screen flex justify-center items-center bg-slate-100 p-6">

      <div className="w-full max-w-2xl bg-white p-6 rounded-xl shadow">

        <h2 className="text-xl font-semibold mb-4">
          Add Stock
        </h2>

        <CommonForm
          api="/stock"
          fields={fields}
        />
      </div>
    </div>
  );
}