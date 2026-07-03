"use client";

import { Suspense } from "react";
import Edit from "@/app/components/CommonEdit";

function UnitEdit() {
  const fields = [
    {
      name: "identifier",
      label: "Unit Name",
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
      urlName="unit"
      fields={fields}
      identifier="identifier"
    />
  );
}

export default function Page() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <UnitEdit />
    </Suspense>
  );
}
