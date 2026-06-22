"use client";

import ListingSkeleton from "@/components/CommonListForm";

export default function ListShelfs() {
  return (
    <ListingSkeleton
      title="Shelfs"
      fields={[]} // Empty array since only identifier is needed
      apis={{
        list: "/shelfs/list",
        delete: "/shelfs/delete",
        toggleStatus: "/shelfs/toggle-status",
      }}
      addPath="/shelfs/add"
      editPathBase="/shelfs/edit/"
      paramKey="identifier"
      deleteStyle="param"
    />
  );
}