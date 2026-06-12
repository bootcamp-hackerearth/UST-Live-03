"use client";

import Add from "@/app/components/CommonAdd";

export default function Page() {
  const fields = [
    {
      name: "identifier",
      label: " Role Name",
      type: "text",
    },
    {
      name: "description",
      label: "description",
      type: "text",
    },
  ];
  return <Add urlName="role" fields={fields} />;
}