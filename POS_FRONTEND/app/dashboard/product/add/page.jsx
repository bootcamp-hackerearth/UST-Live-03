"use client";

import CommonForm from "@/components/CommonForm";
import PRODUCT_FIELDS from "@/components/productFields";

export default function ProductAddPage() {

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">

      <div className="w-full max-w-4xl bg-white border rounded-2xl shadow-xl p-6">

        <div className="mb-6">
          <h1 className="text-xl font-semibold text-slate-800">
            Add Product
          </h1>
          <p className="text-sm text-gray-500">
            Create new product
          </p>
        </div>

        <CommonForm
          api="/product"
          mode="add"
          fields={PRODUCT_FIELDS}
        />

      </div>

    </div>
  );
}