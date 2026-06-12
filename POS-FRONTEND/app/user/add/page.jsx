"use client";

import { useState, useMemo } from "react";
import AddFormSkeleton from "@/components/AddSkeleton";
import MultiDropDown from "@/components/dropdowns/MultiDropDown";

export default function AddUser() {

  const [roles, setRoles] = useState([]);

  const extraFields = useMemo(() => [
    {
      key: "username",
      label: "Username",
      type: "text",
      required: true,
      validate: (val) => {
        const emailRegex = /^[^@\s]+@[^@\s]+\.[^@\s]+$/; // NOSONAR
        if (!emailRegex.test(val)) return "Enter a valid email address (e.g. user@gmail.com).";
        return null;
      },
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
      validate: (val) => {
        if (!/^\d{10}$/.test(val)) return "Phone number must be exactly 10 digits.";
        return null;
      },
    },
    {
      key: "password",
      label: "Password",
      type: "password",
      required: true,
    },
    {
      key: "roles",
      label: "Assign Role(s)",
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
  ], [roles]);

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