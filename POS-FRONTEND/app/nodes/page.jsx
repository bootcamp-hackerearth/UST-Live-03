"use client";

import CommonList from "@/components/CommonList";

export default function NodeList() {

  const fields = [

    {
      name: "identifier",
      type: "text",
      placeholder: "Node Name",
      hardCoded: "false",
      hardCodedArray: [],
      required: true,
      readOnly: false,
    },

    {
      name: "path",
      type: "text",
      placeholder: "/home/nodes",
      hardCoded: "false",
      hardCodedArray: [],
      required: true,
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
      required: true,
      readOnly: false,
    },

  ];

  const dropdownApis = {
    roles:
      "http://localhost:8080/api/role/list",
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
      apiUrl="http://localhost:8080/api/node/list"
      deleteUrl="http://localhost:8080/api/node/delete"
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