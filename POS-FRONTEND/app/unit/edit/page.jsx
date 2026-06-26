"use client";

import Edit from "@/app/components/CommonEdit";

export default function Page() {

  const fields = [
    {
      name: "identifier",
      label: "Unit Name",
      type: "text",
      readOnly: true,
    },

    {
      name: "description",
      label: "Description",
      type: "text",
    },
  ];

  return (
    <Edit
      urlName="unit"
      fields={fields}
      identifier="identifier"
    />
  );
}