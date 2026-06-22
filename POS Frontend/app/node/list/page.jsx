"use client";

import ListingSkeleton from "@/components/CommonListForm";

export default function ListNode() {
  return (
    <ListingSkeleton
      title="Nodes"
      fields={["identifier", "path"]}
      apis={{
        list: "/node/list",
        delete: "/node/delete",
        toggleStatus: "/node/toggle-status",
      }}
      addPath="/node/add"
      editPathBase="/node/edit/"
      paramKey="identifier"
    />
  );
}
