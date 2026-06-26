"use client";

import ListTemplate from "../components/ListTemplate";
import { auditColumns } from "../components/auditColumns";

export default function CategoryList() {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Identifier", field: "identifier" },
    { label: "Supercategory", field: "supercategory" },
    { label: "Status", field: "status" },
    ...auditColumns(),
  ];

  return (
    <ListTemplate
      title="Category Management"
      columns={columns}
      urlName="category"
      showStatus={true}
      editKey="id"
      deleteKey="identifier"
      deleteParam="identifier"
      statusKey="identifier"
      addButtonLabel="Category"
      pageSize={3}
    />
  );
}

