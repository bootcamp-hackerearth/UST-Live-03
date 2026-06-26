"use client";

import Add from "@/app/components/CommonAdd";

export default function Page() {

  const fields = [
    {
      name: "identifier",
      label: "Unit Name",
      type: "text",
      required: true,
    },

    {
      name: "description",
      label: "Description",
      type: "text",
    },

    {
      name: "status",
      label: "Status",
      type: "select",
      required: true,
      options: [
        { value: true, label: "Active" },
        { value: false, label: "Inactive" },
      ],
    },
  ];

  return (
    <Add
      urlName="unit"
      fields={fields}
    />
  );
}