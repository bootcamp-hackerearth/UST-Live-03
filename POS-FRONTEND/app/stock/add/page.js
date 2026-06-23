"use client";

import React from "react";
import AddFormSkeleton from "@/app/components/AddFormSkeleton";
import { stockFields } from "@/app/stock/stockField";

export default function AddStock() {
  return (
    <AddFormSkeleton
      title="Stock"
      apiPath="stock"
      fields={stockFields}
    />
  );
}