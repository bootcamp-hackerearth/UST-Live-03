"use client";

import { useParams } from "next/navigation";
import CommonForm from "@/components/CommonForm";

export default function EditStockPage() {

  const { identifier } = useParams();

  const fields = [
    {
      name: "product",
      label: "Product",
      type: "select",
      api: "/product/list"
    },
    {
      name: "quantity",
      label: "Quantity",
      type: "text"
    },

    {
      name: "warehouse",
      label: "Warehouse",
      type: "select",
      api: "/warehouse/list"
    },
  ];

  return (
    <div className="min-h-screen flex justify-center items-center bg-slate-100 p-6">

      <div className="w-full max-w-2xl bg-white p-6 rounded-xl shadow">

        <h2 className="text-xl font-semibold mb-4">
          Update Stock
        </h2>

        <CommonForm
          api="/stock"
          mode="edit"
          identifier={identifier}
          fields={fields}
        />
      </div>
    </div>
  );
}