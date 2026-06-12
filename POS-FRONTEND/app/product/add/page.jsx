"use client";

import Add from "@/app/components/CommonAdd";
import { productFields } from "@/app/components/ProductField";

export default function Page() {
  return <Add urlName="product" fields={productFields} />;
}