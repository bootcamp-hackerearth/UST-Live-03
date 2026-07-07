"use client";

import { Suspense } from "react";
import Edit from "@/app/components/CommonEdit";

function RackEdit() {
  const fields = [
    {
      name: "identifier",
      label: "Rack Name",
      type: "text",
      readOnly: true,
    },
    {
      name: "shelfs",
      label: "Shelves",
      type: "multiDropdown",
      api: "/shelf/list-active",
      required: true,
    },
    {
      name: "description",
      label: "Description",
      type: "text",
    },
  ];

  return (
    <Edit
      urlName="rack"
      fields={fields}
      identifier="identifier"
    />
  );
}

export default function Page() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <RackEdit />
    </Suspense>
  );
}