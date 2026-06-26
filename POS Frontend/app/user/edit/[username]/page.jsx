"use client";
 
import { useState } from "react";
import EditFormSkeleton from "@/components/CommonEditForm";
import MultiDropDown from "@/components/dropdowns/CommonMultiDropDown";
 
export default function EditUser() {
 
  const [roles, setRoles] = useState([]);
 
  const extraFields = [
    {
      key: "name",
      label: "Full Name",
      type: "text",
    },
    {
      key: "phoneNo",
      label: "Phone Number",
      type: "text",
      validate: (val) => /^\d{10}$/.test(val) ? "" : "Phone number must be exactly 10 digits.",
    },
    {
      key: "roles",
      type: "custom",
      label: "Roles",
      component: (
        <MultiDropDown
          label="Assign Role(s)"
          apiUrl="/role/findByStatus"
          valueField="identifier"
          labelField="identifier"
          selectedValues={roles}
          onChange={(val) => setRoles(val)}
        />
      ),
    },
  ];
 
  return (
    <EditFormSkeleton
      title="User"
      apiPath="user"
      paramName="username"
      identifierField="username"
      extraFields={extraFields}
      extraData={{ roles }}
      setters={{ roles: setRoles }}
    />
  );
}