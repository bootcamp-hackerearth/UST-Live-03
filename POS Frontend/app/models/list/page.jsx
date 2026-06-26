"use client";

import ListingSkeleton from "@/components/CommonListForm";

export default function ListModels() {
  return (
    <ListingSkeleton
      title="Models"
      fields={[]} 
      apis={{
        list: "/models/list",
        delete: "/models/delete",
        toggleStatus: "/models/toggle-status",
      }}
      addPath="/models/add"
      editPathBase="/models/edit/"
      paramKey="identifier"
      deleteStyle="param" 
    />
  );
}