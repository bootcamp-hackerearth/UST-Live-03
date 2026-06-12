import ListTemplate from "../components/ListTemplate";

export default function RoleList() {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Identifier", field: "identifier" },
    { label: "Status", field: "status" },
  ];

  return (
    <ListTemplate
      title="Role Management"
      columns={columns}
      urlName="role"
      showStatus={true}
      editKey="identifier"
      deleteKey="identifier"
      deleteParam="identifier"
      statusKey="identifier"
      addButtonLabel="Role"
      pageSize={10}
    />
  );
}