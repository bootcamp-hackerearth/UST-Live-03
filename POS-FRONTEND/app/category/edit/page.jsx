"use client";

import { useMemo } from "react";
import { useSearchParams } from "next/navigation";
import CommonUpdateTemplate from "../../components/CommonUpdateTemplate";

export default function CategoryEdit() {
  const searchParams = useSearchParams();
  const identifier = searchParams?.get("identifier") || "";

  const extraFields = useMemo(
    () => [
      {
        key: "supercategory",
        label: "Super Category",
        type: "select",
        apiPath: "category",
        placeholder: "Select super category",
        required: false,
      },
    ],
    []
  );

  if (!identifier) {
    return (
      <div className="w-full max-w-3xl mx-auto py-16 text-center text-slate-600">
        No category selected for editing.
      </div>
    );
  }
  return (
    <CommonUpdateTemplate
      title="Category"
      apiPath="category"
      recordId={identifier}
      recordParam="identifier"
      identifierKey="identifier"
      identifierLabel="Identifier"
      extraFields={extraFields}
      showDescription={false}
      identifierEditable={false}
      onSuccessPath="/category"
    />
  );
}