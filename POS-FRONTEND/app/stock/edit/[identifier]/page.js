
"use client";

import React from "react";
import EditFormSkeleton from "@/app/components/EditFormSkeleton";
import { stockFields } from "@/app/stock/stockField";

export default function EditStock() {
  return (
    <EditFormSkeleton
      title="Stock"
      apiPath="stock"
      fields={stockFields}
    />
  );
}