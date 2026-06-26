"use client";

import Edit from "@/app/components/CommonEdit";

export default function Page() {

  const fields = [
    {
      name: "identifier",
      label: "Rack Name",
      type: "text",
      readOnly: true, 
    },
     {
      name: "shelfs",   
      label: "shelves",
      type: "multiDropdown",  
      api: "/shelf/list-active",
      required: true,
    },
    {
      name: "description",
      label: "description",
      type: "text",
    },
  ];

  return (
    <Edit
      urlName="rack"
      fields={fields}
      identifier="identifier"
    />
  );
}