"use client";

import ListingSkeleton from "../../components/ListingSkeleton";

export default function NodeListPage() {
  return (
    <ListingSkeleton
      title="Node"
      fields={["path", "roles"]}
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