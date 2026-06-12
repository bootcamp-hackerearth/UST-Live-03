"use client";

import React from "react";
import Add from "../../../components/add";
import MultiDropdown from "@/components/MultiDropdown";

function RolesFieldAdd(formData, onChange) {
  return (
    <MultiDropdown
      value={formData.roles || []}
      label="Roles"
      apiPath="role"
      required
      onChange={(val) => onChange(val)}
    />
  );
}

function AddUser() {
  const extraFields = [
    {
      key: "name",
      label: "NAME",
      required: true,
    },
    {
      key: "username",
      label: "USERNAME",
      required: true,
    },
    {
      key: "phoneNo",
      label: "PHONE NUMBER",
      type: "number",
      required: true,
    },
    {
      key: "password",
      label: "PASSWORD",
      type: "password",
      required: true,
    },
    {
      key: "roles",
      type: "custom",
      component: RolesFieldAdd,
    },
  ];

  return (
    <Add
      title="USERS"
      apiPath="user"
      extraFields={extraFields}
      showDescription={false}
      hideIdentifier={true}
    />
  );
}
export default AddUser;
