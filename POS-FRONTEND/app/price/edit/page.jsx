"use client";

import { useMemo } from "react";
import { useSearchParams } from "next/navigation";
import CommonUpdateTemplate from "../../components/CommonUpdateTemplate";

export default function PriceEdit() {
  const searchParams = useSearchParams();
  const identifier = searchParams?.get("identifier") || "";

  const extraFields = useMemo(
    () => [
      {
        key: "costprice",
        label: "Cost Price",
        type: "text",
        placeholder: "Enter cost price",
        required: true,
      },
      {
        key: "sellingprice",
        label: "Selling Price",
        type: "text",
        placeholder: "Enter selling price",
        required: true,
      },
      {
        key: "mrpprice",
        label: "MRP Price",
        type: "text",
        placeholder: "Enter MRP price",
        required: true,
      },
    ],
    []
  );

  if (!identifier) {
    return (
      <div className="w-full max-w-3xl mx-auto py-16 text-center text-slate-600">
        No price selected for editing.
      </div>
    );
  }

  return (
    <CommonUpdateTemplate
      title="Price"
      apiPath="price"
      recordId={identifier}
      recordParam="identifier"
      recordGetEndpoint="identifier"
      identifierKey="identifier"
      identifierLabel="Identifier"
      identifierType="select"
      identifierApiPath="product"
      identifierApiEndpoint="active"
      identifierValueKey="identifier"
      identifierLabelKey="identifier"
      identifierEditable={false}
      extraFields={extraFields}
      showDescription={false}
      onSuccessPath="/price"
    />
  );
}