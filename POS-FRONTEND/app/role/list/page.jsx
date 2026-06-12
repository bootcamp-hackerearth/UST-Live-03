"use client";

import ListingSkeleton from "@/components/ListingSkeleton";

export default function ListRole() {
  return (
    <ListingSkeleton
      title="Roles"
      fields={["description"]}
      apis={{
        list: "/role/list",
        delete: "/role/delete",
        toggleStatus: "/role/toggle-status",
      }}
      addPath="/role/add"
      editPathBase="/role/edit/"
      paramKey="identifier"
      deleteStyle="param" 
      identifierLabel="Role Name"
    />
  );
}