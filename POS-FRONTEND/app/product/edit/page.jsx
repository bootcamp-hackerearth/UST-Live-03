"use client";

import Edit from "@/app/components/CommonEdit";
import { productFields } from "@/app/components/ProductField";

export default function Page() {
  return (
    <Edit
      urlName="product"
      fields={productFields}
      identifier="identifier"
    />
  );
}