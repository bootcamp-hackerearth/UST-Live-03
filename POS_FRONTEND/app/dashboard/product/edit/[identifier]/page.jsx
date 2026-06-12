"use client";

import { useParams } from "next/navigation";
import CommonForm from "@/components/CommonForm";
import PRODUCT_FIELDS from "@/components/productFields";

export default function ProductEditPage() {

  const { identifier } = useParams();

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">

      <div className="w-full max-w-4xl bg-white border rounded-2xl shadow-xl p-6">

        <div className="mb-6">
          <h1 className="text-xl font-semibold text-slate-800">
            Edit Product
          </h1>
          <p className="text-sm text-gray-500">
            Update product details
          </p>
        </div>

        <CommonForm
          api="/product"
          mode="edit"
          identifier={identifier}
          fields={PRODUCT_FIELDS}
        />

      </div>

    </div>
  );
}