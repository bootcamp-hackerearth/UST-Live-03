import EditFormSkeleton from "@/app/components/EditFormSkeleton";   

export default function EditNode() {
  const fields = [
    {
      name: "path",
      label: "Path",
      type: "text",
      required: true,
    },
    {
      name: "roles",
      label: "Roles",
      type: "select",
      api: "role",
      multiple: true,
      optionLabel: "identifier",
      optionValue: "identifier",
    },
  ];

  return (
    <EditFormSkeleton
      title="Node"
      apiPath="node"
      paramKey="identifier"
      getParamKey="identifier"
      fields={fields}
    />
  );
}