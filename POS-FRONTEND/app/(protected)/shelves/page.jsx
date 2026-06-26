"use client";
import CommonList from "@/components/table/CommonList";
const shelfColumns = [
  {header: "Shelf Name",field: "identifier"},
  {header: "Description",field: "description"},
];
export default function ShelvesPage() {
  return (
    <CommonList
      title="Shelves"
      subtitle="Manage shelves"
      entity="shelf"
      addPath="/shelves/add"
      editPath="/shelves/edit"
      columns={shelfColumns}
      showToggle={true}
    />
  );
}