"use client";

import ListingSkeleton from "@/components/CommonListForm";

export default function ListShelfs() {
  return (
    <ListingSkeleton
      title="Shelfs"
      fields={[]} 
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