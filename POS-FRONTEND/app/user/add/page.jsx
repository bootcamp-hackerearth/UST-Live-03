"use client";

import Add from "@/app/components/CommonAdd";

export default function Page() {
  const fields = [
    
    {
      name: "name",
      label: "Name",
      type: "text",
    },
    {
      name: "username",
      label: "Username",
      type: "email",
    },
    {
      name: "roles",
      label: "Roles",
      type: "multiDropdown",
      api: "/role/list", 
    },
    {
      name: "phoneNo",
      label: "Phone Number",
      type: "text",
    },
   {
  name: "password",
  label: "Password",
  type: "password", 
}
  ];
  return <Add urlName="user" fields={fields} />;
}