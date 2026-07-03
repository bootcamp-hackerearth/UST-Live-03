"use client";

import { Suspense } from "react";
import Edit from "@/app/components/CommonEdit";
import { productFields } from "@/app/components/ProductField";

function ProductEdit() {
  return (
    <Edit
      urlName="product"
      fields={productFields}
      identifier="identifier"
    />
  );
}

export default function Page() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <ProductEdit />
    </Suspense>
  );
}
