"use client";

import { useParams } from "next/navigation";
import CommonForm from "@/components/CommonForm";

export default function PriceEditPage() {
  const { identifier } = useParams();

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
      options: [
        { identifier: "COST_PRICE" },
        { identifier: "SELLING_PRICE" },
        { identifier: "MRP" }
      ],
      required: true
    }
  ];

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">

      <div className="w-full max-w-4xl bg-white border rounded-2xl shadow-xl p-6">

        <div className="mb-6">
          <h1 className="text-xl font-semibold text-slate-800">
            Edit Price
          </h1>
          <p className="text-sm text-gray-500">
            Update pricing
          </p>
        </div>

        <CommonForm
          api="/price"
          mode="edit"
          identifier={identifier}
          fields={fields}
        />

      </div>
    </div>
  );
}