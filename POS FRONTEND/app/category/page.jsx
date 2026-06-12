import ListTemplate from "../components/ListTemplate";

export default function CategoryList() {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Identifier", field: "identifier" },
    { label: "Supercategory", field: "supercategory" },
    { label: "Status", field: "status" },
  ];

  return (
    <ListTemplate
      title="Category Management"
      columns={columns}
      urlName="category"
      showStatus={true}
      editKey="id"
      deleteKey="id"
      deleteParam="id"
      addButtonLabel="Category"
      pageSize={3}
    />
  );
}