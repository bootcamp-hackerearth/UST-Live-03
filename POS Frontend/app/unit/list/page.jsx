"use client";

import ListingSkeleton from "@/components/CommonListForm";

export default function ListUnit() {
  return (
    <ListingSkeleton
      title="Units"
      fields={[]} // Add specific fields here if UnitDto has more than just identifier
      apis={{
        list: "/unit/list",
        delete: "/unit/delete",
        toggleStatus: "/unit/toggle-status",
      }}
      addPath="/unit/add"
      editPathBase="/unit/edit/"
      paramKey="identifier"
      deleteStyle="param"

    />
  );
}