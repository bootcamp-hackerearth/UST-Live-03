"use client";
import CrudPage from "@/components/CrudPage";
import { crudConfigs } from "@/lib/crudConfigs";

export default function CategoriesPage() {
  return <CrudPage config={crudConfigs.categories()} />;
}
