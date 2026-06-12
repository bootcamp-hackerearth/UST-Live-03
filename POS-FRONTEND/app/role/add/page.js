"use client";
 
import AddForm from "@/app/components/CommonAddPage";
 
export default function AddRolePage() {
  const fields = [
    {
      name: "identifier",
      label: "Role Identifier",
      type: "text",
    },
    {
      name: "description",
      label: "Description",
      type: "text",
    },
  ];
 
  return (
    <AddForm
      title="Add Role"
      submitApi="/api/role/add"   
      redirectRoute="/role/list"     
      fields={fields}
      initialValues={{
        identifier: "",
        description: "",
      }}
      submitButtonText="Save Role"
    />
  );
}
 