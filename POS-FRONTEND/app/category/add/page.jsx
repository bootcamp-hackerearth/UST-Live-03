"use client";

import Add from "@/app/components/CommonAdd";

export default function Page() {
  const fields = [
    {
      name: "identifier",
      label: "Category Name",
      type: "text",
    },
    {
      name: "superCategory",   
      label: "Parent Category",
      type: "singleDropdown",  
      api: "/category/list-active",
      required: false,

    },
  ];
  return <Add urlName="category" fields={fields} />;
}