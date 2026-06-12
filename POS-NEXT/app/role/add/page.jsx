import AddEditForm from "@/components/AddEditForm";

export default function RoleAdd() {

  const fields = [

    { name: "identifier", type: "text", placeholder: "Role Identifier", required: true, readOnly: false },
    { name: "description", type: "text", placeholder: "Role Description", required: true, readOnly: false },

  ];
  const dropdownApis = {};

  return (

    <AddEditForm
      title="Role"
      fields={fields}
      apiRoute="role"
      dropdownApis={dropdownApis}
      method="add"
    />

  );
};
