"use client";

import Edit from "@/app/components/CommonEdit";

export default function Page() {
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