"use client";

import CommonAddTemplate from "../../components/CommonAddTemplate";

export default function UserAdd() {
  const extraFields = [
    {
      key: "name",
      label: "Name",
      type: "text",
      placeholder: "Enter full name",
      required: true,
    },
    {
      key: "phoneNo",
      label: "Phone Number",
      type: "tel",
      placeholder: "Enter 10-digit phone number",
      required: true,
      pattern: "[0-9]{10}",
      maxLength: 10,
      title: "Phone number must be exactly 10 digits",
    },
    {
      key: "password",
      label: "Password",
      type: "password",
      placeholder: "Enter password",
      required: true,
    },
    {
      key: "roles",
      label: "Role",
      type: "multiselect",
      apiEndpoint: "/role/findByStatus",
      required: true,
    },
  ];

  return (
    <CommonAddTemplate
      title="User"
      apiPath="user"
      identifierKey="username"
      identifierLabel="Username"
      extraFields={extraFields}
      onSuccessPath="/user"
    />
  );
}