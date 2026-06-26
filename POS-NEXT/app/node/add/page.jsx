import AddEditForm from "@/components/AddEditForm";

export default function NodeAdd() {

  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
  const fields = [
    {
      name: "identifier", type: "text", placeholder: "Node name",
      required: true, readOnly: false
    },
    {
      name: "path", type: "text", placeholder: "eg: /path/to/node", pattern: "^/[a-zA-Z0-9_-]+(/[a-zA-Z0-9_-]+)*$", patternMessage: "Invalid path format",
      required: true, readOnly: false
    },
    {
      name: "roles", type: "select", placeholder: "Roles", dataKey: "roles",
      hardCoded: false, multiple: true, hardCodedArray: [], required: true, readOnly: false
    }
  ];

  const dropdownApis = {
    roles: `${baseUrl}/role/getactive`
  };

  return (

    <AddEditForm
      title="Node"
      fields={fields}
      apiRoute="node"
      dropdownApis={dropdownApis}
      method="add"/>
  );
};