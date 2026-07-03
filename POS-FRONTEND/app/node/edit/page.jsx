"use client";

import { Suspense } from "react";
import Edit from "@/app/components/CommonEdit";

function NodeEdit() {
  const fields = [
    {
      name: "identifier",
      label: "Node Name",
      type: "text",
    },
    {
      name: "path",
      label: "Path",
      type: "text",
    },
    {
      name: "roles",
      label: "Roles",
      type: "multiDropdown",
      api: "/role/list",
    },
  ];

  return (
    <Edit
      urlName="node"
      fields={fields}
      identifier="identifier"
    />
  );
}

export default function Page() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <NodeEdit />
    </Suspense>
  );
}
