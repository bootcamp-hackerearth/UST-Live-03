"use client";

import Add from "@/app/components/CommonAdd";

export default function Page() {
  const fields = [
    {
      name: "identifier",
      label: "Brand Name",
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
    {
      name: "description",
      label: "Description",
      type: "text",
    },
  ];

  return <Add urlName="brand" fields={fields} />;
}