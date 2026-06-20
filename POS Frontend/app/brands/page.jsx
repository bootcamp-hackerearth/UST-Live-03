"use client";
import CrudPage from "@/components/CrudPage";
import { crudConfigs } from "@/lib/crudConfigs";

export default function BrandsPage() {
  return <CrudPage config={crudConfigs.brands()} />;
}
