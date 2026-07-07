"use client";

import CommonList from "@/components/CommonList";
import { requiredValidation } from "@/validation/validation";

export default function RoleList() {

  const fields = [

    {
      name: "identifier",
      type: "text",
      placeholder: "Role Name",
      hardCoded: "false",
      hardCodedArray: [],
      validation: requiredValidation,
      readOnly: false,
    },

    {
      name: "description",
      type: "text",
      placeholder: "Role Description",
      hardCoded: "false",
      hardCodedArray: [],
      validation: requiredValidation,
      readOnly: false,
    },

  ];

  const dropdownApis = {};

  const columns = [

    {
      key: "id",
      label: "ID",
    },

    {
      key: "identifier",
      label: "Role",
    },

    {
      key: "description",
      label: "Description",
    },

  ];

  return (

    <CommonList
      title="Roles"
      subtitle="Manage role details"
      apiRoute="role"
      columns={columns}
      searchKeys={[
        "identifier",
        "description",
      ]}
      fields={fields}
      dropdownApis={dropdownApis}
    />

  );
}