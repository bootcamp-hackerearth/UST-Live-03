"use client";

import ListingSkeleton from "@/components/CommonListForm";

export default function ListWareHouse() {
  return (
    <ListingSkeleton
      title="Warehouses"
      fields={["location", "manager"]}
      apis={{
        list: "/wareHouse/list",
        delete: "/wareHouse/delete",
        toggleStatus: "/wareHouse/toggle-status",
      }}
      addPath="/wareHouse/add"
      editPathBase="/wareHouse/edit/"
      paramKey="identifier"
      deleteStyle="param"
    />
  );
}