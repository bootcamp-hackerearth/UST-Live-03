"use client";
 
import ListingSkeleton from "@/components/CommonListForm";
 
export default function ListUser() {
  return (
    <ListingSkeleton
      title="Users"
      fields={[
        "name",
        "phoneNo",
        "roles",
      ]}
      apis={{
        list: "/user/list",
        delete: "/user/delete",
        toggleStatus: "/user/toggle-status",
      }}
      addPath="/user/add"
      editPathBase="/user/edit/"
      paramKey="username"
      identifierLabel="Username"
      deleteStyle="param"
    />
  );
}
 