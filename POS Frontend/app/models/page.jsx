"use client";
import CrudPage from "@/components/CrudPage";
import { crudConfigs } from "@/lib/crudConfigs";

export default function ModelsPage() {
  return <CrudPage config={crudConfigs.models()} />;
}
