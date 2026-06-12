"use client";

import Add from "@/app/components/CommonAdd";

export default function Page() {
  const fields = [
    {
      name: "identifier",
      label: " Node Name",
      type: "text",
    },
    {
      name: "path",
      label: "path",
      type: "text",
    },
     {
      name: "roles",
      label: "roles",
      type: "multiDropdown",
      api: "/role/list",
    },
  ];
  return <Add urlName="node" fields={fields} />;
}