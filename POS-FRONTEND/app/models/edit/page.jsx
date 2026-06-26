"use client";

import Edit from "@/app/components/CommonEdit";

export default function Page() {

  const fields = [
    {
      name: "identifier",
      label: "Model Name",
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
      urlName="models"
      fields={fields}
      identifier="identifier"
    />
  );
}