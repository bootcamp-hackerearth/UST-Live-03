"use client";

import Edit from "@/app/components/CommonEdit";

export default function Page() {
  const fields = [
    {
      name: "name",
      label: "Name",
      type: "text",
    },
    {
      name: "username",
      label: "Username (Email)",
      type: "text",             
      validation: "email",    
      readOnly: true,  
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
      validation: "phone",      
    },
  ];
  return <Edit urlName="user" fields={fields} identifier="identifier" />;
}