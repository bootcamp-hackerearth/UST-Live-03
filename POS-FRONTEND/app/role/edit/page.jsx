"use client";

import { Suspense } from "react";
import Edit from "@/app/components/CommonEdit";

function RoleEdit() {
  const fields = [
    {
      name: "identifier",
      label: "Role Name",
      type: "text",
    },
    {
      name: "description",
      label: "Description",
      type: "text",
    },
  ];

  return (
    <Edit
      urlName="role"
      fields={fields}
      identifier="identifier"
    />
  );
}

export default function Page() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <RoleEdit />
    </Suspense>
  );
}
