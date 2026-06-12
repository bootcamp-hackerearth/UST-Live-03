"use client";
import { useMemo } from "react";
import { useSearchParams } from "next/navigation";
import CommonUpdateTemplate from "../../components/CommonUpdateTemplate";
 
export default function ProductEdit() {
  const searchParams = useSearchParams();
  const identifier = searchParams?.get("identifier") || "";
 
  const extraFields = useMemo(
    () => [
      {
        key: "productname",
        label: "Product Name",
        type: "text",
        placeholder: "Enter product name",
        required: true,
      },
      {
        key: "brand",
        label: "Brand",
        type: "select",
        apiEndpoint: "/brand/findByStatus",
        required: true,
      },
      {
        key: "model",
        label: "Model",
        type: "select",
        apiEndpoint: "/model/findByStatus",
        required: true,
      },
      {
        key: "category",
        label: "Category",
        type: "select",
        apiEndpoint: "/category/subcategory",
        required: true,
      },
      {
        key: "unit",
        label: "Unit",
        type: "select",
        apiEndpoint: "/unit/findByStatus",
        required: true,
      },
    ],
    []
  );
 
  if (!identifier) {
    return (
      <div className="w-full max-w-3xl mx-auto py-16 text-center text-slate-600">
        No product selected for editing.
      </div>
    );
  }
 
  return (
    <CommonUpdateTemplate
      title="Product"
      apiPath="product"
      recordId={identifier}
      recordParam="identifier"
      extraFields={extraFields}
      showDescription={false}
      identifierEditable={false}
      onSuccessPath="/product"
    />
  );
}
 
 