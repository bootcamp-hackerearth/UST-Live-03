"use client";
import CrudPage from "@/components/CrudPage";
import { crudConfigs } from "@/lib/crudConfigs";

export default function ShelvesPage() {
  const config = crudConfigs.shelves();
  config.idKey = "id";
  config.fields = [{ key: "id", label: "ID", hideInForm: true }, ...config.fields];
  return <CrudPage config={config} />;
}