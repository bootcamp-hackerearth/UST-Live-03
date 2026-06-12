"use client";
import ListingSkeleton from "../../components/ListingSkeleton";

export default function CategoryListPage() {
  return (
    <ListingSkeleton
      title="Category"
      fields={["superCategory"]}
      apis={{
        list: "/category/list",
        delete: "/category/delete",
        toggleStatus: "/category/toggle-status",
      }}
      addPath="/category/add"
      editPathBase="/category/edit/"
    />
  );
}