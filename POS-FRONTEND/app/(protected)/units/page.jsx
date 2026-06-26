"use client";
import CommonList from "@/components/table/CommonList";
const unitColumns = [
  {header: "Unit Name",field: "identifier"},
  {header: "Description",field: "description"},
];
export default function UnitsPage() {
  return (
    <CommonList
      title="Units"
      subtitle="Manage units"
      entity="unit"
      addPath="/units/add"
      editPath="/units/edit"
      columns={unitColumns}
      showToggle={true}
    />
  );
}