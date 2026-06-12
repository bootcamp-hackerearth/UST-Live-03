"use client";

import Edit from "@/app/components/CommonEdit";

export default function Page() {
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