"use client";
import CrudPage from "@/components/CrudPage";
import { crudConfigs } from "@/lib/crudConfigs";

export default function RacksPage() {
  return <CrudPage config={crudConfigs.racks()} />;
}