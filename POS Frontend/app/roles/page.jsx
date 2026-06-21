"use client";
import CrudPage from "@/components/CrudPage";
import { crudConfigs } from "@/lib/crudConfigs";

export default function RolesPage() {
  const config = crudConfigs.roles();
  config.listEndpoint = "/api/role/list";
  config.getEndpoint = (id) => `/api/role/${id}`;
  config.saveEndpoint = "/api/role/save";
  config.updateEndpoint = (id) => `/api/role/update/${id}`;
  config.deleteEndpoint = (id) => `/api/role/delete/${id}`;
  config.toggleEndpoint = null;
  config.fields = [{ key: "id", label: "ID", hideInForm: true }, ...config.fields];
  return <CrudPage config={config} />;
}