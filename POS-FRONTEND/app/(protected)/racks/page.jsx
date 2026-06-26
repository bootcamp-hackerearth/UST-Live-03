"use client";
import CommonList from "@/components/table/CommonList";

const rackColumns = [
  {header: "Rack Name",field: "identifier"},
  {header: "Description",field: "description"},
  {header: "Shelves",field: "shelves"},
];
export default function RacksPage() {
  return (
    <CommonList
      title="Racks"
      subtitle="Manage racks"
      entity="rack"
      addPath="/racks/add"
      editPath="/racks/edit"
      columns={rackColumns}
      showToggle={true}
    />
  );
}