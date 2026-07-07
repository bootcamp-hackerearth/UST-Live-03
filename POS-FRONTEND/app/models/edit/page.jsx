"use client";

import { Suspense } from "react";
import Edit from "@/app/components/CommonEdit";

function ModelEdit() {
  const fields = [
    {
      name: "identifier",
      label: "Model Name",
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
      urlName="models"
      fields={fields}
      identifier="identifier"
    />
  );
}

export default function Page() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <ModelEdit />
    </Suspense>
  );
}