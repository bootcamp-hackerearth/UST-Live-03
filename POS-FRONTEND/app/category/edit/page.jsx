"use client";

import { Suspense } from "react";
import Edit from "@/app/components/CommonEdit";

function CategoryEdit() {
  const fields = [
    {
      name: "identifier",
      label: "Category Name",
      type: "text",
    },
    {
      name: "superCategory",
      label: "Parent Category",
      type: "singleDropdown",
      api: "/category/list-active",
      required: false,
    },
  ];

  return (
    <Edit
      urlName="category"
      fields={fields}
      identifier="identifier"
    />
  );
}

export default function Page() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <CategoryEdit />
    </Suspense>
  );
}