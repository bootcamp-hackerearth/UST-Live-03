"use client";

import CommonAddTemplate from "../../components/CommonAddTemplate";

export default function RoleAdd() {
  const extraFields = [
    {
      key: "description",
      label: "Description",
      type: "text",
      placeholder: "Enter description",
      required: false,
    },
  ];

  return (
    <CommonAddTemplate
      title="Role"
      apiPath="role"
      identifierKey="identifier"
      identifierLabel="Identifier"
      extraFields={extraFields}
      onSuccessPath="/role"
    />
  );
}