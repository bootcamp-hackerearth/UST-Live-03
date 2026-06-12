"use client";

import CommonList from "../../components/CommonList";

export default function CategoryPage() {
  return (
    <CommonList
      title="Category Management"

      apiUrl="/api/category/list"
      method="POST"

      deleteApi="/api/category/delete"
      deleteParam="identifier"

      editRoute="/category/edit/:identifier"
      addRoute="/category/add"

      columns={[
        { header: "ID", field: "id" },
        { header: "Identifier", field: "identifier" },

        {
          header: "Super Category",
          field: "superCategory",
        },

      ]}
    />
  );
}