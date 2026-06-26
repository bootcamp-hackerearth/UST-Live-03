"use client";
 
import { useState } from "react";
import AddFormSkeleton from "@/components/CommonAddForm";
import MultiDropDown from "@/components/dropdowns/CommonMultiDropDown";
 
export default function AddUser() {
 
  const [roles, setRoles] = useState([]);
 
  const extraFields = [
    {
      key: "username",
      label: "Username",
      type: "text",
      required: true,
      validate: (val) => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(val) ? "" : "Please enter a valid email address.",
    },
    {
      key: "name",
      label: "Full Name",
      type: "text",
      required: true,
    },
    {
      key: "phoneNo",
      label: "Phone Number",
      type: "text",
      required: true,
      validate: (val) => /^\d{10}$/.test(val) ? "" : "Phone number must be exactly 10 digits.",
    },
    {
      key: "password",
      label: "Password",
      type: "password",
      required: true,
    },
    {
      key: "roles",
      type: "custom",
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
    <AddFormSkeleton
      title="User"
      apiPath="user"
      apiEndpoint="register"
      showIdentifier={false}
      extraFields={extraFields}
      extraData={{ roles }}
    />
  );
}