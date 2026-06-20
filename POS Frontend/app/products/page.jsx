"use client";
import CrudPage from "@/components/CrudPage";
import { getProductsConfig } from "@/lib/crudConfigs";

export default function ProductsPage() {
  return <CrudPage config={getProductsConfig()} />;
}
