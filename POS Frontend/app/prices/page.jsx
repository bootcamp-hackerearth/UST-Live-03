"use client";
import CrudPage from "@/components/CrudPage";
import { crudConfigs } from "@/lib/crudConfigs";

export default function PricesPage() {
  return <CrudPage config={crudConfigs.prices()} />;
}
