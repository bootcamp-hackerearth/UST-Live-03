"use client";

import AddFormSkeleton from "@/components/CommonAddForm";
import { useProductFields } from "@/components/useProductFields";

export default function AddProduct() {
  const { extraFields, extraData } = useProductFields();

  return (
    <AddFormSkeleton
      title="Product"
      apiPath="product"
      extraFields={extraFields}
      extraData={extraData}
    />
  );
}