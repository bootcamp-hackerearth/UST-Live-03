"use client";

import CommonForm from "@/components/CommonForm";

export default function PriceAddPage() {

  const fields = [
    {
      name: "product",
      label: "Product",
      type: "select",
      api: "/product/list",
      required: true
    },

    {
      name: "priceAmount",
      label: "Price",
      type: "text",
      required: true
    },

    {
      name: "priceType",
      label: "Price Type",
      type: "select",
      required: true,
      options: [
        { identifier: "COST_PRICE" },
        { identifier: "SELLING_PRICE" },
        { identifier: "MRP" }
      ]
    }
  ];

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">

      <div className="w-full max-w-4xl bg-white border rounded-2xl shadow-xl p-6">

        <div className="mb-6">
          <h1 className="text-xl font-semibold text-slate-800">
            Add Price
          </h1>
          <p className="text-sm text-gray-500">
            Create pricing record
          </p>
        </div>

        <CommonForm
          api="/price"
          mode="add"
          fields={fields}
        />

      </div>
    </div>
  );
}