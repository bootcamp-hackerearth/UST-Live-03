"use client";
import CrudPage from "@/components/CrudPage";
import { crudConfigs } from "@/lib/crudConfigs";

export default function NodesPage() {
  return <CrudPage config={crudConfigs.nodes()} />;
}
