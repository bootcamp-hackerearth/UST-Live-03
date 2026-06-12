"use client";

import CommonAddTemplate from "../../components/CommonAddTemplate";

export default function NodeAdd() {
  const extraFields = [
    {
      key: "path",
      label: "Path",
      type: "text",
      placeholder: "Enter node path",
      required: true,
    },
    {
      key: "roles",
      label: "Roles",
      type: "multiselect",
      apiEndpoint: "/role/findByStatus",
      required: false,
    },
    {
      key: "status",
      label: "Status",
      type: "select",
      options: [
        { value: "true", label: "Active" },
        { value: "false", label: "Inactive" },
      ],
      valueType: "boolean",
      required: true,
    },
  ];

  return (
    <CommonAddTemplate
      title="Node"
      apiPath="node"
      extraFields={extraFields}
      identifierKey="identifier"
      identifierLabel="Identifier"
      onSuccessPath="/node"
    />
  );
}
