"use client";
import CrudPage from "@/components/CrudPage";
import { crudConfigs } from "@/lib/crudConfigs";

export default function StocksPage() {
  return <CrudPage config={crudConfigs.stocks()} />;
}
