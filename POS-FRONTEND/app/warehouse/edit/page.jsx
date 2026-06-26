"use client";

import Edit from "@/app/components/CommonEdit";

export default function Page() {

  const fields = [
    {
      name: "identifier",
      label: "Warehouse Name",
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