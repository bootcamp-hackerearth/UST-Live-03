"use client";
import CrudPage from "@/components/CrudPage";
import { crudConfigs } from "@/lib/crudConfigs";

export default function WarehousesPage() {
  const config = crudConfigs.warehouses();
  config.fields[0].key = "identifier";
  config.fields[0].label = "Warehouse Code";
  config.fields[1].key = "name";
  config.fields[1].label = "Warehouse Name";
  config.fields[2].key = "location";
  config.fields[2].label = "Location";
  return <CrudPage config={config} />;
}