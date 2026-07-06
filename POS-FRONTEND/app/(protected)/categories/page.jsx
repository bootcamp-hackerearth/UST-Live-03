"use client";

import CommonList from "@/components/table/CommonList";

const categoryColumns = [

  {
    header: "Category",
    field: "identifier",
  },

  {
    header: "Super Categories",
    field: "superCategory",
    render: (row) => {

      if (!row.superCategory) {
        return "-";
      }

      if (Array.isArray(row.superCategory)) {
        return row.superCategory.join(", ");
      }

      return row.superCategory;
    },
  },
];

export default function CategoriesPage() {

  return (
    <CommonList

      title="Categories"
      subtitle="Manage product categories"
      entity="category"
      addPath="/categories/add"
      editPath="/categories/edit"
      columns={categoryColumns}
      showToggle={true}
    />
  );

}
