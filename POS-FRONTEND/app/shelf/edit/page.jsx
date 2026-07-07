"use client";

import { Suspense } from "react";
import Edit from "@/app/components/CommonEdit";

function ShelfEdit() {
  const fields = [
    {
      name: "identifier",
      label: "Shelf Name",
      type: "text",
      readOnly: true,
    },
    {
      name: "description",
      label: "Description",
      type: "text",
    },
  ];

  return (
    <Edit
      urlName="shelf"
      fields={fields}
      identifier="identifier"
    />
  );
}

export default function Page() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <ShelfEdit />
    </Suspense>
  );
}