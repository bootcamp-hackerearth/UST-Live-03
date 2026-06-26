"use client";

import Edit from "@/app/components/CommonEdit";

export default function Page() {

  const fields = [
    {
      name: "identifier",
      label: "Shelf Name",
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
      urlName="shelf"
      fields={fields}
      identifier="identifier"
    />
  );
}