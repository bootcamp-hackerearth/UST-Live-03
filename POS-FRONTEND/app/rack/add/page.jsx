"use client";

import Add from "@/app/components/CommonAdd";

export default function Page() {
  const fields = [
    {
      name: "identifier",
      label: "Rack Name",
      type: "text",
    },
    {
      name: "shelfs",   
      label: "shelves",
      type: "multiDropdown",  
      api: "/shelf/list-active",
      required: true,
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
      label: "description",
      type: "text",
    },
  ];

  return <Add urlName="rack" fields={fields} />;
}