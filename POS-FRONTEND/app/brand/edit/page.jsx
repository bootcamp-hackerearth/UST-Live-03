"use client";

import Edit from "@/app/components/CommonEdit";

export default function Page() {
  const fields = [
    {
      name: "identifier",
      label: "Brand Name",
      type: "text",
      readOnly: true, 
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

  return (
    <Edit
      urlName="brand"
      fields={fields}
      identifier="identifier"
    />
  );
}