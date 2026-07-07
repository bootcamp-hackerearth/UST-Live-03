"use client";

import { Suspense } from "react";
import Edit from "@/app/components/CommonEdit";

function BrandEditContent() {
  const fields = [
    {
      name: "identifier",
      label: "Brand Name",
      type: "text",
      readOnly: true,
    },
    {
      name: "status",
      label: "Status",
      type: "select",
      options: [
        { value: true, label: "Active" },
        { value: false, label: "Inactive" },
      ],
    },
    {
      name: "description",
      label: "Description",
      type: "text",
    },
  ];

  return (
    <Edit
      urlName="brand"
      fields={fields}
      identifier="identifier"
    />
  );
}

export default function Page() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <BrandEditContent />
    </Suspense>
  );
}