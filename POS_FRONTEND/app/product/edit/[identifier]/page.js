"use client";

import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";
import { getProductFields } from "@/app/components/productFormConfig";

export default function ProductEditPage() {
  return (
    <CommonEditPage
      title="Edit Product"
      fetchApi={async (identifier) => {
        const res = await api.get("/api/product/get", {
          params: { identifier },
        });
        return res;
      }}
      updateApi={(data) => {
        return api.put("/api/product/update", data);
      }}
      redirectRoute="/product/list"
      identifierParam="identifier"
      fields={getProductFields(true)} 
    />
  );
}