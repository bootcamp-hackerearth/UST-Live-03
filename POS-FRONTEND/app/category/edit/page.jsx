"use client";

import Edit from "@/app/components/CommonEdit";

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
  return (
    <Edit
      urlName="category"
      fields={fields}
      identifier="identifier" 
    />
  );
}
