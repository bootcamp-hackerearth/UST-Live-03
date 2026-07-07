"use client";

import CommonList from "@/components/CommonList";
import { requiredValidation } from "@/validation/validation";

export default function NodeList() {

  const fields = [

    {
      name: "identifier",
      type: "text",
      placeholder: "Node Name",
      hardCoded: "false",
      hardCodedArray: [],
      validation: requiredValidation,
      readOnly: false,
    },

    {
      name: "path",
      type: "text",
      placeholder: "/home/nodes",
      hardCoded: "false",
      hardCodedArray: [],
      validation: requiredValidation,
      readOnly: false,
    },

    {
      name: "roles",
      type: "select",
      placeholder: "Select Roles",
      dataKey: "roles",
      hardCoded: "false",
      multiple: true,
      hardCodedArray: [],
      validation: requiredValidation,
      readOnly: false,
    },

  ];

  const dropdownApis = {
    roles:
      process.env.NEXT_PUBLIC_BASE_URL+"/role/list",
  };

  const columns = [

    {
      key: "identifier",
      label: "Identifier",
    },

    {
      key: "path",
      label: "Path",
    },

  ];

  return (

    <CommonList
      title="Nodes"
      subtitle="Manage node details"
      apiRoute="node"
      columns={columns}
      searchKeys={[
        "identifier",
        "path",
      ]}
      fields={fields}
      dropdownApis={dropdownApis}
    />

  );
}