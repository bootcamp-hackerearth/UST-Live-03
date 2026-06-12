"use client";
 
import ListingSkeleton from "@/components/CommonListForm";
 
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
      editPathBase="/role/edit?identifier="
      paramKey="identifier"
      identifierLabel="Role Name"
      deleteStyle="param"
    />
  );
}