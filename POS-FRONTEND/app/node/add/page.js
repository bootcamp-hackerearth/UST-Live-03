"use client";

import CommonAddPage from "@/app/components/CommonAddPage";
import { dropdownField } from "@/app/components/DropdownFieldHelper";
import { entitySubmit } from "@/app/components/EntitySubmitHelper";

export default function NodeAddPage() {
  const handleSubmit = async (data) =>
    entitySubmit(
      "/api/node/add",
      {
        ...data,
        status: data.status === true || data.status === "true",
      },
      "Node added successfully"
    );

  return (
    <CommonAddPage
      title="Add Node"
      submitApi={handleSubmit}
      redirectRoute="/node/list"
      initialValues={{
        identifier: "",
        path: "",
        roles: [],
        status: true,
      }}
      fields={[
        {
          label: "Identifier",
          name: "identifier",
          type: "text",
        },
        {
          label: "Path",
          name: "path",
          type: "text",
          placeholder: "Enter node path",
        },
        dropdownField(
          "Roles",
          "roles",
          "/api/role/list",
          {
            multiple: true,
          }
        ),
      ]}
    />
  );
}