"use client";

import { Suspense } from "react";
import Edit from "@/app/components/CommonEdit";

function WarehouseEdit() {
  const fields = [
    {
      name: "identifier",
      label: "Warehouse Code",
      type: "text",
      readOnly: true,
    },
    {
      name: "country",
      label: "Country",
      type: "text",
    },
    {
      name: "region",
      label: "Region",
      type: "text",
    },
  ];

  return (
    <Edit
      urlName="warehouse"
      fields={fields}
      identifier="identifier"
    />
  );
}

export default function Page() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <WarehouseEdit />
    </Suspense>
  );
}