"use client";
import CommonList from "@/components/table/CommonList";

const warehouseColumns = [
  {header: "Warehouse Name",field: "identifier"},
  {header: "Country",field: "country"},
  {header: "Region",field: "region"},
  {header: "Address",field: "address"},
  {header: "Phone Number",field: "phoneNumber"},
];
export default function WarehousesPage() {
  return (
    <CommonList
      title="Warehouses"
      subtitle="Manage warehouses"
      entity="warehouse"
      addPath="/warehouses/add"
      editPath="/warehouses/edit"
      columns={warehouseColumns}
      showToggle={true}
    />
  );
}