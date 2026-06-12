"use client";

import ListTemplate from "../components/ListTemplate";

export default function CategoryList() {
  const columns = [
    {
      label: "Identifier",
      field: "identifier",
    },
    {
      label: "Super Category",
      field: "supercategory",
    },
    {
      label: "Status",
      field: "status",
    },
  ];

  return (
    <ListTemplate
      title="Category Management"
      columns={columns}
      urlName="category"
      showStatus={true}
      editKey="identifier"
      deleteKey="identifier"
      deleteParam="identifier"
      rowKey="identifier"
      addButtonLabel="Category"
      pageSize={10}
      sortField="identifier"
      editUseQuery={true}
    />
  );
}
