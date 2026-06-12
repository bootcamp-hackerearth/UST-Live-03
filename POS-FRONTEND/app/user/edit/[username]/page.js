import EditFormSkeleton from "@/app/components/EditFormSkeleton";

export default function UserEdit() {
  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
      disabled: true, 

    },
    {
      name: "name",
      label: "Name",
      type: "text",
    },
    {
      name: "phoneNo",
      label: "Phone Number",
      type: "text",
    },
    {
      name: "roles",
      label: "Roles",
      type: "select",
      multiple: true,
      api: "role",
      endpoint: "findByStatus",   
      optionLabel: "identifier",
      optionValue: "identifier",
    },
  ];

  return (
    <EditFormSkeleton
      title="User"
      apiPath="user"
      fields={fields}
      paramKey="username"       
      getParamKey="username"   
    />
  );
}