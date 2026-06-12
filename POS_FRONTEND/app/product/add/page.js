"use client";

import CommonAddPage from "@/app/components/CommonAddPage";
import api from "@/app/services/api";
import { getProductFields } from "@/app/components/productFormConfig";

export default function ProductAddPage() {
  return (
    <CommonAddPage
      title="Add Product"
      submitApi={(data) => {
        console.log("Submitting Product:", data);

        if (
          !data.identifier ||
          !data.category ||
          !data.brand ||
          !data.model ||
          !data.unit
        ) {
          alert("Please fill all required fields");
          return;
        }

        return api.post("/api/product/add", {
          ...data,
          quantity: Number(data.quantity) || 0,
          status: data.status === true || data.status === "true",
        });
      }}
      redirectRoute="/product/list"
      initialValues={{
        identifier: "",
        category: "",
        brand: "",
        model: "",
        unit: "",
        quantity: 0,
        status: true,
      }}
      fields={getProductFields(false)} 
    />
  );
}