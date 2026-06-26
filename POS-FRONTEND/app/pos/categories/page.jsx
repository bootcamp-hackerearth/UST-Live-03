// app/pos/categories/page.jsx

"use client";

import BaseListForm from "../../../components/lists/BaseListForm";

export default function CategoryListPage() {
  const columns = [
    { key: "identifier", label: "Category Name" },
    { 
      key: "superCategory", 
      label: "Super Category",
      render: (val) => val ? (
          <span className="font-mono text-md text-[#231F20]">{val}</span>
      ) : (
          <span className="text-[10px] font-semibold text-gray-400 bg-gray-100 px-2 py-0.5 rounded-full">
              N/A
          </span>
      )
    },
    { key: "status", label: "Status" },
  ];

  return (
    <BaseListForm
      title="Categories"
      entity="category"
      columns={columns}
      addPath="/pos/categories/add"
      editPath="/pos/categories/edit"
      identifierKey="identifier"
    />
  );
}