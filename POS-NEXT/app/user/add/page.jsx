"use client";

import AddEditForm from "@/components/AddEditForm";

export default function UserAdd() {

  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
  const fields = [
    {
      name: "name",
      type: "text", placeholder: "name",
      minlength: 3, patternMessage: "Name must be at least 3 characters", dataKey: "name", required: true
    },
    {
      name: "description",
      type: "text", placeholder: "Description",
      minlength: 3, dataKey: "description", required: true
    },
    {
      name: "username",
      type: "email", placeholder: "Email",
      pattern: String.raw`^[^\s@]+@(ust\.com|gmail\.com)$`, patternMessage: "Please enter a valid UST email address", dataKey: "email", required: true
    },
    {
      name: "roles",
      type: "select", placeholder: "roles",
      hardCoded: false, hardCodedArray: [], multiple: true, dataKey: "roles", required: true
    },
    {
      name: "phoneNo",
      type: "text", placeholder: "Phone Number",
      patternMessage: "Phone number must be 10 digits", minlength: 10, pattern: "^[0-9]{10}$", dataKey: "phoneNo", required: true
    },
    {
      name: "password",
      type: "password", placeholder: "Enter Password",
      pattern: String.raw`^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$`, patternMessage: "Password must be at least 8 characters along and contain at least one uppercase letter, one lowercase letter, and one number", dataKey: "password", required: true
    }
  ];

  const dropdownApis = {

    roles: `${baseUrl}/role/getactive`
  };

  return (
    
      <AddEditForm
        title="User"
        fields={fields}
        apiRoute="user"
        dropdownApis={dropdownApis}
        method="add"
      /> 
  );
};

