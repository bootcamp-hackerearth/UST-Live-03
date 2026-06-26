"use client";

import Add from "@/app/components/CommonAdd";

export default function Page() {
  const fields = [
    {
      name: "identifier",
      label: "Warehouse Name",
      type: "text",
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
    {
      name: "status",
      label: "Status",
      type: "select",
      options: [
        { value: true, label: "Active" },
        { value: false, label: "Inactive" },
      ],
    },
  ];

  return <Add urlName="warehouse" fields={fields} />;
}