"use client";

import EditFormSkeleton from "@/components/CommonEditForm";
import { useProductFields } from "@/components/useProductFields";

export default function EditProduct() {
  const { extraFields, extraData, setters } = useProductFields();

  return (
    <EditFormSkeleton
      title="Product"
      apiPath="product"
      extraFields={extraFields}
      extraData={extraData}
      setters={setters}
    />
  );
}