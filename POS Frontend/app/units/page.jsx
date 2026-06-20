"use client";
import CrudPage from "@/components/CrudPage";
import { crudConfigs } from "@/lib/crudConfigs";

export default function UnitsPage() {
  return <CrudPage config={crudConfigs.units()} />;
}
