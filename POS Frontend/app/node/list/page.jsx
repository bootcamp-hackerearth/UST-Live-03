"use client";
 
import ListingSkeleton from "@/components/CommonListForm";
 
export default function ListNode() {
  return (
    <ListingSkeleton
      title="Nodes"
      fields={[
        "path",
        "roles",
      ]}
      apis={{
        list: "/node/list",
        delete: "/node/delete",
        toggleStatus: "/node/toggle-status",
      }}
      addPath="/node/add"
      editPathBase="/node/edit/"
    />
  );
}